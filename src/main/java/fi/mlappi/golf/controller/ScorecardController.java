package fi.mlappi.golf.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import fi.mlappi.golf.model.Game;
import fi.mlappi.golf.model.Scorecard;
import fi.mlappi.golf.service.GameService;
import fi.mlappi.golf.service.PlayerService;
import fi.mlappi.golf.service.ScorecardService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class ScorecardController {

	@Autowired
	GameService gameService;
	@Autowired
	ScorecardService scoreService;
	@Autowired
	PlayerService playerService;

	@RequestMapping("/score/new")
	public String add(ModelMap model) {
		log.debug("add a new score");
		Scorecard s = new Scorecard();
		s.setGame(gameService.getAllGames().get(0));
		s.setPlayer(playerService.getAllPlayers().get(0));
		model.addAttribute("score", s);
		model.addAttribute("playerId", s.getPlayer().getId());
		model.addAttribute("gameId", s.getGame().getId());
		return "new-scorecard";
	}

	@RequestMapping(value = "/score/edit/{id}")
	public String edit(ModelMap model, @PathVariable("id") long id) {
		log.debug("edit score " + id);
		Scorecard s = scoreService.find(id);
		model.addAttribute("score", s);
		model.addAttribute("playerId", s.getPlayer().getId());
		model.addAttribute("gameId", s.getGame().getId());
		return "new-scorecard";
	}

	@RequestMapping(value = "/score/list/{id}")
	public String scoreList(ModelMap model, @PathVariable("id") long id) {
		log.debug("scoreList " + id);
		model.addAttribute("game", gameService.find(id));
		model.addAttribute("scoreList", scoreService.findByGameId(id));
		return "list-scores";
	}

	@RequestMapping(value = "/score/remove/{id}")
	public String remove(ModelMap model, @PathVariable("id") long id) {
		log.debug("remove game " + id);
		Scorecard s = scoreService.find(id);
		Game game = s.getGame();
		scoreService.delete(id);
		model.put("message", "The scorecard removed.");
		return scoreList(model, game.getId());
	}

	@RequestMapping(value = "/score/save", method = RequestMethod.POST)
	public String save(ModelMap model, @ModelAttribute("score") @Valid Scorecard score,
			@RequestParam("gameId") Long gameId,
			@RequestParam("playerId") Long playerId,
			BindingResult result) {
		log.debug("save: " + score.toString());
		log.debug("game: " + gameId);
		log.debug("player: " + playerId);
				
		score.setGame(gameService.find(gameId));
		score.setPlayer(playerService.find(playerId));
		boolean newScorecard = score.getId() == null ? true : false;
		if (!result.hasErrors()) {
			scoreService.save(score);
			if (newScorecard)
				model.put("message", "The new scorecard has been successfully created.");
			else
				model.put("message", "The scorecard has been successfully updated.");
			model.put("idScore", score.getId());
		} else {
			log.warn("jotain väärin... " + result.getFieldError());
			return "new-scorecard";
		}
		return scoreList(model, gameId);
	}

}
