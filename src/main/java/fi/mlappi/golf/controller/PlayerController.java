package fi.mlappi.golf.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import fi.mlappi.golf.model.Player;
import fi.mlappi.golf.service.PlayerService;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class PlayerController {

	@Autowired
	PlayerService playerService;

	@RequestMapping({"/"})
	public String home(ModelMap model) {
		return "index";
	}

	@RequestMapping({"/player"})
	public String players(ModelMap model) {
		log.debug("get all players...");
		List<Player> players = playerService.getAllPlayers();
		model.addAttribute("players", players);
		return "list-players";
	}

	@RequestMapping("/player/new")
	public String add(ModelMap model) {
		log.debug("add a new player");
		model.addAttribute("editPlayer", new Player());
		return "new-player";
	}

	@RequestMapping(value = "/player/edit/{id}")
	public String edit(ModelMap model, @PathVariable("id") long id) {
		log.debug("edit player " + id);
		model.addAttribute("player", playerService.find(id));
		return "new-player";
	}

	@RequestMapping(value = "/player/remove/{id}")
	public String remove(ModelMap model, @PathVariable("id") long id) {
		log.debug("remove player " + id);
		if(playerService.findScorecards(id).isEmpty()) {
			playerService.delete(id);
			model.put("message", "The player has been successfully removed.");
		}
		else {
			model.put("errormessage", "The player has played some rounds. Remove all scorecards first.");
		}
		return players(model);
	}

	@RequestMapping(value = "/player/save", method = RequestMethod.POST)
	public String save(ModelMap model, @ModelAttribute("player") @Valid Player player, BindingResult result) {
		log.debug("save: " + player.toString());
		boolean newPlayer = player.getId() == null ? true : false;
		if (!result.hasErrors()) {			
			playerService.save(player);
			if(newPlayer)
				model.put("message", "The new player has been successfully created.");				
			else
				model.put("message", "The player has been successfully updated.");
			model.put("idPlayer", player.getId());
		}
		else {
			log.warn("jotain väärin... " +result.getFieldError());
			return "new-player";
		}
		return players(model);
	}

	@RequestMapping(value = "/player/search", method = RequestMethod.POST)
	public String search(ModelMap model, @ModelAttribute("playerName") String playerName) {
		log.debug("search: " + playerName);
		model.addAttribute("players", playerService.search(playerName));
		return "list-players";
	}

}
