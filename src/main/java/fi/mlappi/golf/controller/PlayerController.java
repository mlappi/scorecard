package fi.mlappi.golf.controller;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fi.mlappi.golf.model.Player;
import fi.mlappi.golf.service.PlayerService;
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
			model.put("message", "Pelaaja on poistettu onnistuneesti.");
		}
		else {
			model.put("errormessage", "Pelaajalla on pelattuja kierroksia. Poista ensin kaikki tuloskortit.");
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
				model.put("message", "Uusi pelaaja on lisätty onnistuneesti.");				
			else
				model.put("message", "Pelaaja on päivitetty onnistuneesti.");
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

	@RequestMapping(value = "/player/import", method = RequestMethod.POST)
	public String importPlayers(@RequestParam("playerList") String playerList,
			RedirectAttributes redirectAttributes) {
		if (playerList == null || playerList.trim().isEmpty()) {
			redirectAttributes.addFlashAttribute("errormessage", "Pelaajalista on tyhjä.");
			return "redirect:/player";
		}
		List<Player> players = parsePlayers(playerList);
		if (players.isEmpty()) {
			redirectAttributes.addFlashAttribute("errormessage", "Listasta ei löytynyt kelvollisia rivejä.");
			return "redirect:/player";
		}
		for (Player player : players) {
			playerService.save(player);
		}
		redirectAttributes.addFlashAttribute("message", "Lisätty " + players.size() + " pelaajaa.");
		return "redirect:/player";
	}

	private List<Player> parsePlayers(String rawList) {
		List<Player> players = new ArrayList<>();
		String[] lines = rawList.split("\\R");
		for (String line : lines) {
			if (line == null) {
				continue;
			}
			String trimmed = line.trim();
			if (trimmed.isEmpty()) {
				continue;
			}
			String[] parts = trimmed.split("\\s+");
			if (parts.length < 2) {
				continue;
			}
			String firstName = parts[0].trim();
			String lastName = String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length)).trim();
			if (firstName.isEmpty() || lastName.isEmpty()) {
				continue;
			}
			if (playerService.existsByName(firstName, lastName)) {
				continue;
			}
			Player player = new Player();
			player.setFirstName(firstName);
			player.setLastName(lastName);
			player.setHcp(0d);
			player.setEmail(null);
			players.add(player);
		}
		return players;
	}

}
