package fi.mlappi.golf.controller;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import fi.mlappi.golf.model.Round;
import fi.mlappi.golf.service.CourseService;
import fi.mlappi.golf.service.GameService;
import fi.mlappi.golf.service.ScorecardService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class GameController  {

	@Autowired
	GameService gameService;
	@Autowired
	ScorecardService scoreService;
	@Autowired
	CourseService courseService;
	
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
		addCourseList(model);		
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
		game.getRound().add(round);		
		model.addAttribute("game", game);
		addCourseList(model);
		
		return "new-game";
	}

	private void addCourseList(ModelMap model) {
		Map<Long,String> courseList = new LinkedHashMap<Long,String>();
		for(Course c : courseService.getAllCourses()) {	
			courseList.put(c.getId(), c.getName());
		}		
		model.addAttribute("courseList", courseList);
	}

	@RequestMapping(value = "/game/edit/{id}")
	public ModelAndView edit(ModelMap model, @PathVariable("id") long id) {
		log.debug("edit game " + id);
		model.addAttribute("game", gameService.find(id));
		addCourseList(model);
		return new ModelAndView("new-game");
	}

	@RequestMapping(value = "/game/remove/{id}")
	public String remove(ModelMap model, @PathVariable("id") long id) {
		log.debug("remove game " + id);		
		Game game = gameService.find(id);
		boolean empty = true;
		for(Round r : game.getRound()) {
			if(!scoreService.findByRoundId(r.getId()).isEmpty()) {
				empty = false;
				break;
			}
		}
		if(empty)
			gameService.delete(id);
		else
			model.put("errormessage", "Can't remove game. Remove all scorecards first.");
		
		List<Game> games = gameService.getAllGames();
		model.addAttribute("games", games);
		return "list-games";
	}

	@RequestMapping(value = "/game/remove-round/{gameId}/{id}")
	public String removeRound(ModelMap model, @PathVariable("gameId") long gameId, @PathVariable("id") long id) {
		if(id > 0) {
			log.debug("remove round " + id);			
			if(scoreService.findByRoundId(id).isEmpty()) {			
				gameService.removeRound(id);
				model.put("message", "The round successfully removed.");
			}
			else {
				model.put("errormessage", "Can't remove round. Remove all scorecards first.");
			}
		}		
		return "new-game";
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
				Course c = courseService.find(round.getCourse().getId());
				round.setCourse(c);
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
		addCourseList(model);
		return "new-game";
	}

	@RequestMapping(value = "/game/search", method = RequestMethod.POST)
	public String search(ModelMap model, @ModelAttribute("name") String name) {
		log.debug("search: " + name);
		model.addAttribute("games", gameService.search(name));
		return "list-games";
	}

}
