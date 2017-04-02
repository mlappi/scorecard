package fi.mlappi.golf.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import fi.mlappi.golf.model.Course;
import fi.mlappi.golf.model.Game;
import fi.mlappi.golf.model.Player;
import fi.mlappi.golf.model.Round;
import fi.mlappi.golf.model.Scorecard;
import fi.mlappi.golf.service.GameService;
import fi.mlappi.golf.service.PlayerService;
import fi.mlappi.golf.service.ScorecardService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class GameController  {

	@Autowired
	GameService gameService;
	ScorecardService scoreService = new ScorecardService();
	
	@RequestMapping("/game")
	public String games(ModelMap model) {
		log.debug("get all games...");
		List<Game> games = gameService.getAllGames();
		model.addAttribute("games", games);
		return "list-games";
	}

	@RequestMapping("/game/new")
	public String add(ModelMap model) {
		log.debug("add a new game");
		Game game = new Game();
		game.setDate(new Date());
		model.addAttribute("game", game);
		
		return "new-game";
	}

	@RequestMapping("/game/addRound/{id}")
	public String addRound(ModelMap model, @PathVariable("id") long id) {
		log.debug("add a new round");
		Game game = gameService.find(id);
		
		Round round = new Round();
		round.setBet(0d);
		round.setDate(new Date());
		round.setName("");
		//TODO: kenttäeditori
		round.setCourse(gameService.getMuurame());				
		game.getRound().add(round);
		
		model.addAttribute("game", game);
		
		return "new-game";
	}

	@RequestMapping(value = "/game/edit/{id}")
	public ModelAndView edit(ModelMap model, @PathVariable("id") long id) {
		log.debug("edit game " + id);
		model.addAttribute("game", gameService.find(id));
		return new ModelAndView("new-game");
		//return new ModelAndView("redirect:/new-game");
	}

	@RequestMapping(value = "/game/remove/{id}")
	public String remove(ModelMap model, @PathVariable("id") long id) {
		log.debug("remove game " + id);
		gameService.delete(id);
		List<Game> games = gameService.getAllGames();
		model.addAttribute("games", games);
		return "list-games";
	}

	@RequestMapping(value = "/game/remove-round/{gameId}/{id}")
	public String removeRound(ModelMap model, @PathVariable("gameId") long gameId, @PathVariable("id") long id) {
		if(id > 0) {
			log.debug("remove round " + id);
			gameService.removeRound(id);
		}
		model.put("message", "The round successfully removed.");
		return "redirect:/game/edit/"+gameId;
	}

	@RequestMapping(value = "/game/save", method = RequestMethod.POST)
	public String save(ModelMap model, @ModelAttribute("game") @Valid Game game, BindingResult result) {
		log.debug("save: " + game.toString());
		boolean newGame = game.getId() == null ? true : false;
		if (!result.hasErrors()) {
			log.debug("rounds " +game.getRound().size());
			for (Round round : game.getRound()) {
				log.debug("save round");
				round.setGame(game);
				round.setCourse(gameService.getMuurame());
				gameService.save(round);				
			}
			gameService.save(game);
			if(newGame)
				model.put("message", "The new game has been successfully created.");				
			else
				model.put("message", "The game has been successfully updated.");
			model.put("idGame", game.getId());
		}
		else {
			for (ObjectError e : result.getAllErrors()) {
				log.error(e.getDefaultMessage());
			}
		}
		return "new-game";//games(model);
	}

	@RequestMapping(value = "/game/search", method = RequestMethod.POST)
	public String search(ModelMap model, @ModelAttribute("name") String name) {
		log.debug("search: " + name);
		model.addAttribute("games", gameService.search(name));
		return "list-games";
	}

	
	//TODO: score service
	private void searchGameAndScores(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		long idGame = Integer.valueOf(req.getParameter("idGame"));
		Round game = null;
		try {
			//game = gameService.getGame(idGame);
		} catch (Exception ex) {
			
		}
		List<Scorecard> scores = scoreService.getAllScorecards();

		double pot = game.getBet() * scores.size();
		for (int i = 1; i < 19; i++) {
			Set<Player> players = getPlayersForLowestScore(scores, i, game);
			if (!players.isEmpty()) {
				game.getWinMap().put(i, players);//
			}
		}

		double holeValue = pot / game.getWinMap().size();

		for (Scorecard scorecard : scores) {
			try {
				Player player = new Player(); //playerService.getPlayer(scorecard.getPlayerId());
				double wins = 0;
				Set<Integer> holeset = game.getWinMap().keySet();
				for (Integer hole : holeset) {
					if (game.getWinMap().get(hole).contains(player.getId())) {
						wins = wins + (holeValue / game.getWinMap().get(hole).size());
						scorecard.getWinners().add(hole);
					}
				}
				if (wins > 0)
					scorecard.setWin(BigDecimal.valueOf(wins).setScale(2, RoundingMode.FLOOR).doubleValue());
				//scorecard.setPlayer(player.getName().substring(0, 1) + ". " + player.getLastName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		scores.sort((Scorecard s1, Scorecard s2) -> Double.valueOf(s2.getWin()).compareTo(Double.valueOf(s1.getWin())));
		req.setAttribute("game", game);
		req.setAttribute("scoreList", scores);

		String nextJSP = "/jsp/list-scores.jsp";
		//RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
		//dispatcher.forward(req, resp);
	}

	private Set<Player> getPlayersForLowestScore(List<Scorecard> scores, int hole, Round game) {
		Set<Player> players = new HashSet<>();
		Player bestPlayer = null;
		long bestScore = 0;
		for (Scorecard scorecard : scores) {
			if (bestScore == 0) {
				bestPlayer = scorecard.getPlayer();
				bestScore = scorecard.getScore(hole);
				players.add(bestPlayer);
			} else {
				if (bestScore > scorecard.getScore(hole)) {
					players.remove(bestPlayer);
					bestPlayer = scorecard.getPlayer();
					bestScore = scorecard.getScore(hole);
					players.add(bestPlayer);
				} else if (bestScore == scorecard.getScore(hole) && game.getPar(hole) <= bestScore) {					
					players.remove(bestPlayer);
					bestPlayer = null;
				}
				/*
				 * Jenkkiskinisääntö, birkulla aina rahaa jos ei ilkkoja
				 */
				else if (bestScore == scorecard.getScore(hole) && game.getPar(hole) > bestScore) {
					bestPlayer = null;
					players.add(scorecard.getPlayer());
				}
			}
		}
		return players;
	}

}
