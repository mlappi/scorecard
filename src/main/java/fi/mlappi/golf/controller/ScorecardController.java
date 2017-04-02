package fi.mlappi.golf.controller;

import java.util.LinkedHashMap;
import java.util.Map;

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
import fi.mlappi.golf.model.Round;
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

	@RequestMapping("/score/add/{id}")
	public String add(ModelMap model, @PathVariable("id") long id) {
		log.debug("add a new score. round:" +id);
		Scorecard s = new Scorecard();
		s.setRound(gameService.findRound(id));
		
		//TODO:
		s.setPlayer(playerService.getAllPlayers().get(0));
		
		addModelValues(model, s);
		
		return "new-scorecard";
	}

	private void addModelValues(ModelMap model, Scorecard s) {
		model.addAttribute("score", s);
		model.addAttribute("playerId", s.getPlayer().getId());
		model.addAttribute("gameId", s.getRound().getGame().getId());
		model.addAttribute("roundId", s.getRound().getId());
	}

	@RequestMapping(value = "/score/edit/{id}")
	public String edit(ModelMap model, @PathVariable("id") long id) {
		log.debug("edit score " + id);
		Scorecard s = scoreService.find(id);
		addModelValues(model, s);
		
		return "new-scorecard";
	}

	@RequestMapping(value = "/score/list/{gameId}")
	public String scoreList(ModelMap model, @PathVariable("gameId") long gameId, @ModelAttribute("round") RoundSelect roundSelect) {
		log.debug("scoreList game id  " + gameId);
		log.debug("round " + roundSelect);
		Game game = gameService.find(gameId);		
		Map<Long,String> roundList = new LinkedHashMap<Long,String>();
		int i = 0;
		for(Round r : game.getRound()) {	
			if(i == 0 && roundSelect.getRoundId() == 0) {
				roundSelect.setRoundId(r.getId());
			}
			roundList.put(r.getId(), r.getName() + " | "+r.getCourseName());
			i++;
		}
		model.addAttribute("game", game);
		model.addAttribute("scoreList", scoreService.findByRoundId(roundSelect.getRoundId()));
		model.addAttribute("roundList", roundList);		
		model.addAttribute("round", roundSelect);
		//model.addAttribute("selectedRoundId", round.getRoundId());
		return "list-scores";
	}

	@RequestMapping(value = "/score/remove/{id}")
	public String remove(ModelMap model, @PathVariable("id") long id) {
		log.debug("remove game " + id);
		Scorecard s = scoreService.find(id);
		Round round = s.getRound();
		scoreService.delete(id);
		model.put("message", "The scorecard removed.");
		RoundSelect rs = new RoundSelect();
		rs.setRoundId(round.getId());
		return "redirect:/score/list/"+round.getGame().getId();
	}

	@RequestMapping(value = "/score/save", method = RequestMethod.POST)
	public String save(ModelMap model, @ModelAttribute("score") @Valid Scorecard score,
			@RequestParam("gameId") Long gameId,
			@RequestParam("roundId") Long roundId,
			@RequestParam("playerId") Long playerId,
			BindingResult result) {
		log.debug("save: " + score.toString());
		log.debug("game: " + gameId);
		log.debug("player: " + playerId);
		log.debug("round: " + roundId);
				
		score.setRound(gameService.findRound(roundId));
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
		RoundSelect rs = new RoundSelect();
		rs.setRoundId(roundId);

		return "redirect:/score/list/"+gameId;
	}

}
