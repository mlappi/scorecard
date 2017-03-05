package fi.mlappi.golf.game;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import fi.mlappi.golf.model.Game;
import fi.mlappi.golf.model.Player;
import fi.mlappi.golf.model.Scorecard;
import fi.mlappi.golf.player.PlayerController;
import fi.mlappi.golf.player.PlayerService;
import fi.mlappi.golf.scorecard.ScorecardService;
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
		model.addAttribute("game", new Game());
		return "new-game";
	}

	@RequestMapping(value = "/game/edit/{id}")
	public String edit(ModelMap model, @PathVariable("id") long id) {
		log.debug("edit game " + id);
		model.addAttribute("game", gameService.find(id));
		return "new-game";
	}

	@RequestMapping(value = "/game/remove/{id}")
	public String remove(ModelMap model, @PathVariable("id") long id) {
		log.debug("remove game " + id);
		gameService.delete(id);
		return games(model);
	}

	@RequestMapping(value = "/game/save", method = RequestMethod.POST)
	public String save(ModelMap model, @ModelAttribute("player") @Valid Game game, BindingResult result) {
		log.debug("save: " + game.toString());
		boolean newGame = game.getId() == null ? true : false;
		if (!result.hasErrors()) {			
			gameService.save(game);
			if(newGame)
				model.put("message", "The new game has been successfully created.");				
			else
				model.put("message", "The game has been successfully updated.");
			model.put("idGame", game.getId());
		}
		return games(model);
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
		Game game = null;
		try {
			//game = gameService.getGame(idGame);
		} catch (Exception ex) {
			
		}
		List<Scorecard> scores = scoreService.getAllScorecards();

		double pot = game.getBet() * scores.size();
		for (int i = 1; i < 19; i++) {
			Set<Long> pIds = getPlayersForLowestScore(scores, i, game);
			if (!pIds.isEmpty()) {
				game.getWinMap().put(i, pIds);
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

	private Set<Long> getPlayersForLowestScore(List<Scorecard> scores, int hole, Game game) {
		Set<Long> players = new HashSet<>();
		long bestPlaeyerId = 0;
		long bestScore = 0;
		for (Scorecard scorecard : scores) {
			if (bestScore == 0) {
				bestPlaeyerId = scorecard.getPlayerId();
				bestScore = scorecard.getScore(hole);
				players.add(bestPlaeyerId);
			} else {
				if (bestScore > scorecard.getScore(hole)) {
					players.remove(bestPlaeyerId);
					bestPlaeyerId = scorecard.getPlayerId();
					bestScore = scorecard.getScore(hole);
					players.add(bestPlaeyerId);
				} else if (bestScore == scorecard.getScore(hole) && game.getPar(hole) <= bestScore) {					
					players.remove(bestPlaeyerId);
					bestPlaeyerId = 0;
				}
				/*
				 * Jenkkiskinisääntö, birkulla aina rahaa jos ei ilkkoja
				 */
				else if (bestScore == scorecard.getScore(hole) && game.getPar(hole) > bestScore) {
					bestPlaeyerId = 0;
					players.add(scorecard.getPlayerId());
				}
			}
		}
		return players;
	}

}
