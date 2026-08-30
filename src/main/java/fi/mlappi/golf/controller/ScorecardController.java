package fi.mlappi.golf.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.mlappi.golf.model.Game;
import fi.mlappi.golf.model.Player;
import fi.mlappi.golf.model.PlayFormat;
import fi.mlappi.golf.model.Round;
import fi.mlappi.golf.model.Scorecard;
import fi.mlappi.golf.service.GameService;
import fi.mlappi.golf.service.GameStatisticsService;
import fi.mlappi.golf.service.PlayerService;
import fi.mlappi.golf.service.ScorecardService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class ScorecardController {

	@Autowired
	GameService gameService;
	@Autowired
	GameStatisticsService gameStatisticsService;
	@Autowired
	ScorecardService scoreService;
	@Autowired
	PlayerService playerService;
	@Autowired
	ObjectMapper objectMapper;

	@RequestMapping("/score/leaderboard/{id}")
	public String leaderboard(ModelMap model, @PathVariable("id") long id,
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "view", required = false) String view) {
		List<LeaderboardScore> scoreList = new ArrayList<>();
		List<Long> rounds = new ArrayList<>();
		Set<Long> teamRounds = new HashSet<>();
		Map<Long, LeaderboardScore> scoreMap = new HashMap<>();
		Map<Long, List<Scorecard>> scorecardsByRound = new LinkedHashMap<>();
		Game game = gameService.find(id);
		for (Round r : game.getRound()) {
			List<Scorecard> scorecards = scoreService.countWins(r.getId());
			scorecardsByRound.put(r.getId(), scorecards);
			rounds.add(r.getId());
			if (scorecards.stream().anyMatch(Scorecard::isTeamScorecard)) {
				teamRounds.add(r.getId());
			}
		}
		Map<Long, Integer> roundIndex = new HashMap<>();
		for (int i = 0; i < rounds.size(); i++) {
			roundIndex.put(rounds.get(i), i);
		}
		for (Round r : game.getRound()) {
			List<Scorecard> scorecards = scorecardsByRound.get(r.getId());
			double playerStake = scoreService.getPlayerStake(r, scorecards);
			Integer index = roundIndex.get(r.getId());
			for (Scorecard s : scorecards) {
				List<Player> competitors = s.getCompetitors();
				if (competitors.isEmpty()) {
					continue;
				}
				double grossShare = s.getWin() / competitors.size();
				double stakeShare = playerStake / competitors.size();
				for (Player player : competitors) {
					LeaderboardScore lbs = scoreMap.computeIfAbsent(player.getId(), playerId -> {
						LeaderboardScore created = new LeaderboardScore();
						created.setName(player.getFirstName() + " " + player.getLastName());
						for (int i = 0; i < rounds.size(); i++) {
							created.getScore().add(null);
						}
						return created;
					});
					lbs.setSkinsWon(lbs.getSkinsWon() + s.getSkinsWon());
					lbs.setGrossTotal(lbs.getGrossTotal() + grossShare);
					lbs.setStakeTotal(lbs.getStakeTotal() + stakeShare);
					lbs.setNetTotal(lbs.getGrossTotal() - lbs.getStakeTotal());
					int total = s.getCountTotal();
					if (index != null) {
						lbs.getScore().set(index, total);
					}
					if (total > 0) {
						lbs.setThru(lbs.getThru() + 18);
						lbs.setRoundsPlayed(lbs.getRoundsPlayed() + 1);
						lbs.setTotalAll(lbs.getTotalAll() + total);
						lbs.setTotal(lbs.getTotal() + (total - s.getRound().getCourse().getCountTotal()));
					}
				}
			}
		}

		scoreList.addAll(scoreMap.values());
		
		if (view == null || (!"money".equalsIgnoreCase(view) && !"stroke".equalsIgnoreCase(view))) {
			view = "totalAll".equalsIgnoreCase(sort) || "total".equalsIgnoreCase(sort)
					|| "average".equalsIgnoreCase(sort) ? "stroke" : "money";
		}
		if ("stroke".equalsIgnoreCase(view) && "totalAll".equalsIgnoreCase(sort)) {
			scoreList.sort((LeaderboardScore s1, LeaderboardScore s2) -> Integer.valueOf(s1.getTotalAll())
					.compareTo(Integer.valueOf(s2.getTotalAll())));
		} else if ("stroke".equalsIgnoreCase(view) && "total".equalsIgnoreCase(sort)) {
			scoreList.sort((LeaderboardScore s1, LeaderboardScore s2) -> Integer.valueOf(s1.getTotal())
					.compareTo(Integer.valueOf(s2.getTotal())));
		} else if ("stroke".equalsIgnoreCase(view)) {
			scoreList.sort((s1, s2) -> {
				int comparison = Double.compare(s1.getAverageToPar(), s2.getAverageToPar());
				if (comparison != 0) {
					return comparison;
				}
				comparison = Integer.compare(s2.getRoundsPlayed(), s1.getRoundsPlayed());
				return comparison != 0 ? comparison : Integer.compare(s1.getTotal(), s2.getTotal());
			});
			sort = "average";
		} else if ("grossTotal".equalsIgnoreCase(sort)) {
			scoreList.sort((s1, s2) -> Double.compare(s2.getGrossTotal(), s1.getGrossTotal()));
		} else if ("stakeTotal".equalsIgnoreCase(sort)) {
			scoreList.sort((s1, s2) -> Double.compare(s2.getStakeTotal(), s1.getStakeTotal()));
		} else {
			scoreList.sort((LeaderboardScore s1, LeaderboardScore s2) -> Double.valueOf(s2.getNetTotal())
					.compareTo(Double.valueOf(s1.getNetTotal())));
			sort = "netTotal";
		}
		model.addAttribute("scores", scoreList);
		model.addAttribute("gameId", id);
		model.addAttribute("rounds", rounds);
		model.addAttribute("teamRounds", teamRounds);
		model.addAttribute("sort", sort);
		model.addAttribute("view", view.toLowerCase());

		return "leaderboard";
	}

	@RequestMapping("/score/birdieboard/{id}")
	public String birdieboard(ModelMap model, @PathVariable("id") long id) {
		Game game = gameService.find(id);
		model.addAttribute("gameId", id);
		model.addAttribute("game", game);
		model.addAttribute("eagles", buildBirdieboardScores(game, -2));
		model.addAttribute("birdies", buildBirdieboardScores(game, -1));
		return "birdieboard";
	}

	@RequestMapping("/score/statistics/{id}")
	public String statistics(ModelMap model, @PathVariable("id") long id) {
		Game game = gameService.find(id);
		model.addAttribute("gameId", id);
		model.addAttribute("game", game);
		model.addAttribute("statistics", gameStatisticsService.calculate(game));
		return "statistics";
	}

	@RequestMapping("/score/add/{id}")
	public String add(ModelMap model, @PathVariable("id") long roundId) {
		log.debug("add a new score. round:" + roundId);
		Scorecard scorecard = new Scorecard();
		scorecard.setRound(gameService.findRound(roundId));
		addModelValues(model, scorecard);

		return "new-scorecard";
	}

	private void addModelValues(ModelMap model, Scorecard s) {
		model.addAttribute("score", s);
		if (s.getPlayer() != null)
			model.addAttribute("playerId", s.getPlayer().getId());
		model.addAttribute("gameId", s.getRound().getGame().getId());
		model.addAttribute("roundId", s.getRound().getId());
		model.addAttribute("game", s.getRound().getGame());
		model.addAttribute("round", s.getRound());

		model.addAttribute("playerList", buildAvailablePlayers(s.getRound()));
		model.addAttribute("allPlayerList", buildPlayerList());
		model.addAttribute("playFormats", PlayFormat.values());

	}

	@RequestMapping(value = "/score/edit/{id}")
	public String edit(ModelMap model, @PathVariable("id") long id) {
		log.debug("edit score " + id);
		Scorecard s = scoreService.find(id);
		addModelValues(model, s);

		return "new-scorecard";
	}

	@RequestMapping(value = "/score/list/{gameId}")
	public String scoreList(ModelMap model, @PathVariable("gameId") long gameId,
			@ModelAttribute("round") RoundSelect roundSelect,
			@RequestParam(value = "sort", required = false) String sort) {
		log.debug("scoreList game id  " + gameId);
		log.debug("round " + roundSelect);
		Game game = gameService.find(gameId);
		Map<Long, String> roundList = new LinkedHashMap<Long, String>();
		int i = 0;
		for (Round r : game.getRound()) {
			if (i == 0 && roundSelect.getRoundId() == 0) {
				roundSelect.setRoundId(r.getId());
			}
			roundList.put(r.getId(), r.getName() + " | " + r.getCourse().getName());
			i++;
		}

		List<Scorecard> scores = scoreService.countWins(roundSelect.getRoundId());
		Round selectedRound = game.getRound(roundSelect.getRoundId());
		double competitorStake = scoreService.getPlayerStake(selectedRound, scores);
		if ("total".equalsIgnoreCase(sort)) {
			scores.sort((Scorecard s1, Scorecard s2) -> Integer.valueOf(s1.getCountTotal()).compareTo(Integer.valueOf(s2.getCountTotal())));
		} else {
			scores.sort((Scorecard s1, Scorecard s2) -> Double.valueOf(s2.getWin()).compareTo(Double.valueOf(s1.getWin())));
			sort = "win";
		}
		model.addAttribute("game", game);
		model.addAttribute("scoreList", scores);
		model.addAttribute("roundList", roundList);
		model.addAttribute("round", roundSelect);
		model.addAttribute("sort", sort);
		model.addAttribute("competitorStake", competitorStake);
		model.addAttribute("importPlayerList", buildAvailablePlayers(game.getRound(roundSelect.getRoundId())));

		return "list-scores";
	}

	@RequestMapping(value = "/score/remove/{id}")
	public String remove(ModelMap model, @PathVariable("id") long id) {
		log.debug("remove game " + id);
		Scorecard s = scoreService.find(id);
		Round round = s.getRound();
		scoreService.delete(id);
		model.put("message", "Tuloskortti on poistettu onnistuneesti.");
		RoundSelect rs = new RoundSelect();
		rs.setRoundId(round.getId());
		return "redirect:/score/list/" + round.getGame().getId();
	}

	@RequestMapping(value = "/score/save", method = RequestMethod.POST)
	    public String save(ModelMap model, @ModelAttribute("score") @Valid Scorecard score,
		    @RequestParam("gameId") Long gameId,
		    @RequestParam(value = "playerId", required = false) Long playerId,
		    @RequestParam("roundId") Long roundId,
		    @RequestParam(value = "playFormat", required = false) PlayFormat playFormat,
		    @RequestParam(value = "teamName", required = false) String teamName,
		    @RequestParam(value = "participantIds", required = false) List<Long> participantIds,
		    BindingResult result) {
		// Ensure related entities are set before logging/saving so service can detect existing records
		score.setRound(gameService.findRound(roundId));
		score.setPlayFormat(playFormat == null ? PlayFormat.INDIVIDUAL : playFormat);
		if (score.isTeamScorecard()) {
			score.setPlayer(null);
			score.setTeamName(normalizeName(teamName));
			score.getParticipants().clear();
			if (participantIds != null) {
				for (Long participantId : new LinkedHashSet<>(participantIds)) {
					Player participant = playerService.find(participantId);
					if (participant != null) {
						score.getParticipants().add(participant);
					}
				}
			}
			if (score.getTeamName().isBlank()) {
				result.rejectValue("teamName", "team.required", "Anna joukkueen nimi");
			}
			if (score.getParticipants().size() < 2) {
				result.reject("participants.required", "Valitse joukkueeseen vähintään kaksi pelaajaa");
			}
			if (score.getExternalCompetitorId() == null || score.getExternalCompetitorId().isBlank()) {
				score.setExternalCompetitorId("manual-" + UUID.randomUUID());
			}
		} else {
			score.setTeamName(null);
			score.setExternalCompetitorId(null);
			score.getParticipants().clear();
		}
		Long pid = playerId;
		if (pid == null && score.getPlayer() != null) {
			pid = score.getPlayer().getId();
		}
		if (!score.isTeamScorecard() && pid != null) {
			score.setPlayer(playerService.find(pid));
		} else if (!score.isTeamScorecard()) {
			// Missing player -- return to form with an error
			result.rejectValue("player", "player.required", "Valitse pelaaja ennen tallennusta");
			addModelValues(model, score);
			return "new-scorecard";
		}
		for (int i = 1; i <= 18; i++) {
			if (score.getScore(i) == null) {
				result.reject("score.missing", "Kaikki reiät tulee täyttää ennen tallennusta");
				addModelValues(model, score);
				return "new-scorecard";
			}
		}
		log.debug("save: " + score.toString());
		log.debug("game: " + gameId);
		log.debug("round: " + roundId);
		boolean newScorecard = score.getId() == null ? true : false;
		if (!result.hasErrors()) {
			scoreService.save(score);
			if (newScorecard)
				model.put("message", "Uusi tuloskortti on lisätty onnistuneesti.");
			else
				model.put("message", "Tuloskortti on päivitetty onnistuneesti.");
			model.put("idScore", score.getId());
		} else {
			log.warn("jotain väärin... " + result.getFieldError());
			addModelValues(model, score);
			return "new-scorecard";
		}
		RoundSelect rs = new RoundSelect();
		rs.setRoundId(roundId);

		// scoreService.countWins(roundId);

		return "redirect:/score/list/" + gameId + "?roundId=" + roundId;
	}

	@RequestMapping(value = "/score/import", method = RequestMethod.POST)
	public String importScores(@RequestParam("gameId") Long gameId,
			@RequestParam("roundId") Long roundId,
			@RequestParam("playerId") Long playerId,
			@RequestParam("rawScores") String rawScores,
			RedirectAttributes redirectAttributes) {
		if (gameId == null || roundId == null) {
			redirectAttributes.addFlashAttribute("error", "peli ja kierros tulee valita.");
			return "redirect:/score/list/" + (gameId != null ? gameId : "");
		}
		Round round = gameService.findRound(roundId);
		if (round == null) {
			redirectAttributes.addFlashAttribute("error", "valittu kierros ei ole kelvollinen.");
			return "redirect:/score/list/" + gameId;
		}
		Map<Long, String> availablePlayers = buildAvailablePlayers(round);
		if (playerId == null || !availablePlayers.containsKey(playerId)) {
			redirectAttributes.addFlashAttribute("error",
					"Valittu pelaaja ei ole kelvollinen tai hänellä on jo tulos kortti tälle kierrokselle.");
			return "redirect:/score/list/" + gameId;
		}
		List<Integer> numbers = parseScoreNumbers(rawScores);
		if (numbers.size() != 18 && numbers.size() != 21) {
			redirectAttributes.addFlashAttribute("error",
					"Importoitavien tulosten määrä on virheellinen. Syötä joko 18 tai 21 lukua.");
			return "redirect:/score/list/" + gameId;
		}
		if (numbers.size() == 21) {
			int outSum = numbers.get(9);
			int inSum = numbers.get(19);
			int totalSum = numbers.get(20);
			int outCalc = sumRange(numbers, 0, 8);
			int inCalc = sumRange(numbers, 10, 18);
			if (outSum != outCalc || inSum != inCalc || totalSum != (outCalc + inCalc)) {
				redirectAttributes.addFlashAttribute("error",
						"Etu- tai takysin summa ei täsmää: OUT " + outCalc + ", IN " + inCalc + ", TOT " + (outCalc + inCalc));
				return "redirect:/score/list/" + gameId;
			}
		}
		Scorecard score = new Scorecard();
		score.setRound(round);
		score.setPlayer(playerService.find(playerId));
		if (numbers.size() == 18) {
			applyHoleScores(score, numbers);
		} else {
			applyHoleScoresWithChecks(score, numbers);
		}
		scoreService.save(score);
		redirectAttributes.addFlashAttribute("message", "Tulokset tallennettu pelaajalle " + availablePlayers.get(playerId) + ".");
		return "redirect:/score/list/" + gameId + "?roundId=" + roundId;
	}

	@RequestMapping(value = "/score/import-json", method = RequestMethod.POST)
	public String importScoresFromJson(@RequestParam("gameId") Long gameId,
			@RequestParam(value = "roundId", required = false) Long roundId,
			@RequestParam(value = "importRound1Id", required = false) Long importRound1Id,
			@RequestParam(value = "importRound2Id", required = false) Long importRound2Id,
			@RequestParam("resultsFile") MultipartFile resultsFile,
			RedirectAttributes redirectAttributes) {
		Long firstRoundId = importRound1Id != null ? importRound1Id : roundId;
		if (gameId == null || firstRoundId == null) {
			redirectAttributes.addFlashAttribute("error", "Peli ja kierros tulee valita.");
			return "redirect:/score/list/" + (gameId != null ? gameId : "");
		}
		Round firstRound = gameService.findRound(firstRoundId);
		Round secondRound = importRound2Id != null && importRound2Id > 0 ? gameService.findRound(importRound2Id) : null;
		if (firstRound == null || (importRound2Id != null && importRound2Id > 0 && secondRound == null)) {
			redirectAttributes.addFlashAttribute("error", "Valittu kierros ei ole kelvollinen.");
			return "redirect:/score/list/" + gameId;
		}
		if (secondRound != null && firstRound.getId().equals(secondRound.getId())) {
			redirectAttributes.addFlashAttribute("error", "Valitse K1 ja K2 tuonnille eri kierrokset.");
			return "redirect:/score/list/" + gameId + "?roundId=" + firstRoundId;
		}
		if (resultsFile == null || resultsFile.isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "Valitse tuotava JSON-tiedosto.");
			return "redirect:/score/list/" + gameId + "?roundId=" + firstRoundId;
		}
		try {
			JsonNode players = objectMapper.readTree(resultsFile.getInputStream())
					.path("data")
					.path("primary")
					.path("players");
			if (!players.isArray()) {
				redirectAttributes.addFlashAttribute("error", "JSON-tiedostosta ei löytynyt pelaajalistaa.");
				return "redirect:/score/list/" + gameId + "?roundId=" + firstRoundId;
			}
			int imported = 0;
			int skipped = 0;
			for (JsonNode playerNode : players) {
				JsonImportCompetitor competitor = parseJsonImportCompetitor(playerNode);
				if (competitor == null) {
					skipped++;
					continue;
				}
				JsonNode jsonRounds = playerNode.path("rounds");
				int importedBeforeFirstRound = imported;
				imported += importJsonRound(jsonRounds, 0, firstRound, competitor);
				if (imported == importedBeforeFirstRound) {
					skipped++;
				}
				if (secondRound != null) {
					int importedBeforeSecondRound = imported;
					imported += importJsonRound(jsonRounds, 1, secondRound, competitor);
					if (imported == importedBeforeSecondRound) {
						skipped++;
					}
				}
			}
			if (imported == 0) {
				redirectAttributes.addFlashAttribute("error", "JSON-tiedostosta ei löytynyt tuotavia tuloksia.");
			} else {
				redirectAttributes.addFlashAttribute("message",
						"Tuotu " + imported + " tuloskorttia. Ohitettu " + skipped + " riviä.");
			}
		} catch (IOException | IllegalArgumentException e) {
			log.warn("json import failed", e);
			redirectAttributes.addFlashAttribute("error", "JSON-tiedoston lukeminen epäonnistui.");
		}
		return "redirect:/score/list/" + gameId + "?roundId=" + firstRoundId;
	}

	private Map<Long, String> buildAvailablePlayers(Round round) {
		Map<Long, String> playerList = new LinkedHashMap<Long, String>();
		if (round == null) {
			return playerList;
		}
		List<Scorecard> existing = scoreService.findByRoundId(round.getId());
		Set<Long> existingIds = new HashSet<>();
		for (Scorecard scorecard : existing) {
			if (scorecard.getPlayer() != null) {
				existingIds.add(scorecard.getPlayer().getId());
			}
		}
		List<Player> players = new ArrayList<>(playerService.getAllPlayers());
		players.sort((p1, p2) -> {
			String f1 = p1.getFirstName() == null ? "" : p1.getFirstName();
			String f2 = p2.getFirstName() == null ? "" : p2.getFirstName();
			int cmp = f1.compareToIgnoreCase(f2);
			if (cmp != 0) {
				return cmp;
			}
			String l1 = p1.getLastName() == null ? "" : p1.getLastName();
			String l2 = p2.getLastName() == null ? "" : p2.getLastName();
			return l1.compareToIgnoreCase(l2);
		});
		for (Player player : players) {
			if (!existingIds.contains(player.getId())) {
				playerList.put(player.getId(), player.getFirstName() + " " + player.getLastName());
			}
		}
		return playerList;
	}

	private Map<Long, String> buildPlayerList() {
		Map<Long, String> playerList = new LinkedHashMap<>();
		List<Player> players = new ArrayList<>(playerService.getAllPlayers());
		players.sort((first, second) -> (first.getFirstName() + " " + first.getLastName())
				.compareToIgnoreCase(second.getFirstName() + " " + second.getLastName()));
		for (Player player : players) {
			playerList.put(player.getId(), player.getFirstName() + " " + player.getLastName());
		}
		return playerList;
	}

	private List<BirdieboardScore> buildBirdieboardScores(Game game, int parDifference) {
		Map<Long, BirdieboardScore> scoreMap = new HashMap<>();
		if (game == null || game.getRound() == null) {
			return new ArrayList<>();
		}
		for (Round round : game.getRound()) {
			List<Scorecard> scorecards = scoreService.findByRoundId(round.getId());
			for (Scorecard scorecard : scorecards) {
				int count = scoreService.countScoresRelativeToPar(scorecard, parDifference);
				for (Player player : scorecard.getCompetitors()) {
					BirdieboardScore boardScore = scoreMap.computeIfAbsent(player.getId(), id -> {
						BirdieboardScore score = new BirdieboardScore();
						score.setName(player.getFirstName() + " " + player.getLastName());
						return score;
					});
					if (scorecard.isTeamScorecard()) {
						boardScore.setTeamTotal(boardScore.getTeamTotal() + count);
					} else {
						boardScore.setIndividualTotal(boardScore.getIndividualTotal() + count);
					}
				}
			}
		}
		List<BirdieboardScore> scores = scoreMap.values().stream()
				.filter(score -> score.getTotal() > 0)
				.toList();
		scores = new ArrayList<>(scores);
		scores.sort((s1, s2) -> {
			int cmp = Integer.compare(s2.getTotal(), s1.getTotal());
			if (cmp != 0) {
				return cmp;
			}
			return s1.getName().compareToIgnoreCase(s2.getName());
		});
		return scores;
	}

	private List<Integer> parseScoreNumbers(String rawScores) {
		List<Integer> numbers = new ArrayList<>();
		if (rawScores == null) {
			return numbers;
		}
		Matcher matcher = Pattern.compile("\\d+").matcher(rawScores);
		while (matcher.find()) {
			numbers.add(Integer.parseInt(matcher.group()));
		}
		return numbers;
	}

	private int sumRange(List<Integer> values, int start, int end) {
		int sum = 0;
		for (int i = start; i <= end; i++) {
			sum += values.get(i);
		}
		return sum;
	}

	private void applyHoleScores(Scorecard score, List<Integer> values) {
		score.setHole1(values.get(0));
		score.setHole2(values.get(1));
		score.setHole3(values.get(2));
		score.setHole4(values.get(3));
		score.setHole5(values.get(4));
		score.setHole6(values.get(5));
		score.setHole7(values.get(6));
		score.setHole8(values.get(7));
		score.setHole9(values.get(8));
		score.setHole10(values.get(9));
		score.setHole11(values.get(10));
		score.setHole12(values.get(11));
		score.setHole13(values.get(12));
		score.setHole14(values.get(13));
		score.setHole15(values.get(14));
		score.setHole16(values.get(15));
		score.setHole17(values.get(16));
		score.setHole18(values.get(17));
	}

	private void applyHoleScoresWithChecks(Scorecard score, List<Integer> values) {
		score.setHole1(values.get(0));
		score.setHole2(values.get(1));
		score.setHole3(values.get(2));
		score.setHole4(values.get(3));
		score.setHole5(values.get(4));
		score.setHole6(values.get(5));
		score.setHole7(values.get(6));
		score.setHole8(values.get(7));
		score.setHole9(values.get(8));
		score.setHole10(values.get(10));
		score.setHole11(values.get(11));
		score.setHole12(values.get(12));
		score.setHole13(values.get(13));
		score.setHole14(values.get(14));
		score.setHole15(values.get(15));
		score.setHole16(values.get(16));
		score.setHole17(values.get(17));
		score.setHole18(values.get(18));
	}

	private int importJsonRound(JsonNode jsonRounds, int jsonRoundIndex, Round targetRound,
			JsonImportCompetitor importCompetitor) {
		if (targetRound == null || !jsonRounds.isArray() || jsonRounds.size() <= jsonRoundIndex) {
			return 0;
		}
		JsonNode roundNode = jsonRounds.get(jsonRoundIndex);
		List<Integer> scores = readScores(roundNode.path("strokes"), targetRound);
		if (scores.size() != 18) {
			return 0;
		}
		Scorecard score = new Scorecard();
		score.setRound(targetRound);
		score.setPlayFormat(importCompetitor.playFormat());
		score.setExternalCompetitorId(importCompetitor.externalCompetitorId());
		if (importCompetitor.playFormat().isTeamFormat()) {
			score.setTeamName(importCompetitor.teamName());
			score.setPlayingHcp(readDouble(roundNode.path("playingHcp")));
			for (JsonImportMember member : importCompetitor.members()) {
				Player participant = playerService.findOrCreateByExternalUserId(member.externalUserId(),
						member.firstName(), member.lastName(), null);
				score.getParticipants().add(participant);
			}
			if (score.getParticipants().size() < 2) {
				return 0;
			}
		} else {
			JsonImportMember member = importCompetitor.members().get(0);
			Player player = playerService.findOrCreateByExternalUserId(member.externalUserId(),
					member.firstName(), member.lastName(), readDouble(roundNode.path("hcp")));
			score.setPlayer(player);
		}
		applyHoleScores(score, scores);
		scoreService.save(score);
		return 1;
	}

	private JsonImportCompetitor parseJsonImportCompetitor(JsonNode playerNode) {
		Long externalUserId = readLong(playerNode.path("userId"));
		String name = normalizeName(playerNode.path("name").asText(""));
		if (externalUserId != null) {
			String[] nameParts = splitPlayerName(name, false);
			return nameParts == null ? null : new JsonImportCompetitor(PlayFormat.INDIVIDUAL,
					String.valueOf(externalUserId), null,
					List.of(new JsonImportMember(externalUserId, nameParts[0], nameParts[1])));
		}
		Long teamId = readLong(playerNode.path("teamId"));
		JsonNode teamNode = playerNode.path("team");
		if (teamId == null || name.isBlank() || !teamNode.isArray()) {
			return null;
		}
		List<JsonImportMember> members = new ArrayList<>();
		for (JsonNode memberNode : teamNode) {
			Long memberId = readLong(memberNode.path("userId"));
			String[] memberName = splitPlayerName(normalizeName(memberNode.path("name").asText("")), false);
			if (memberId != null && memberName != null) {
				members.add(new JsonImportMember(memberId, memberName[0], memberName[1]));
			}
		}
		return members.size() < 2 ? null : new JsonImportCompetitor(PlayFormat.GREENSOME,
				String.valueOf(teamId), name, members);
	}

	private Double readDouble(JsonNode node) {
		if (node == null || node.isMissingNode() || node.isNull()) {
			return 0d;
		}
		if (node.isNumber()) {
			return node.asDouble();
		}
		String value = node.asText("").trim().replace(',', '.');
		return value.isEmpty() ? 0d : Double.valueOf(value);
	}

	private Long readLong(JsonNode node) {
		if (node == null || node.isMissingNode() || node.isNull()) {
			return null;
		}
		if (node.isNumber()) {
			return node.asLong();
		}
		String value = node.asText("").trim();
		if (value.isEmpty()) {
			return null;
		}
		return Long.valueOf(value);
	}

	private List<Integer> readScores(JsonNode scoreNode, Round targetRound) {
		List<Integer> scores = new ArrayList<>();
		if (!scoreNode.isArray() || scoreNode.size() != 18) {
			return scores;
		}
		int hole = 1;
		for (JsonNode value : scoreNode) {
			String score = value.asText("").trim();
			if (score.isEmpty()) {
				return new ArrayList<>();
			}
			if ("-".equals(score)) {
				scores.add((int) targetRound.getPar(hole) + 5);
			} else {
				scores.add(Integer.parseInt(score));
			}
			hole++;
		}
		return scores;
	}

	private String normalizeName(String name) {
		return name == null ? "" : name.trim().replaceAll("\\s+", " ");
	}

	private String[] splitPlayerName(String name, boolean team) {
		if (name == null || name.isBlank()) {
			return null;
		}
		String[] parts = name.split("\\s+", 2);
		if (parts.length < 2) {
			return team ? new String[] { "Joukkue", parts[0] } : null;
		}
		return new String[] { parts[0], parts[1] };
	}

	private record JsonImportCompetitor(PlayFormat playFormat, String externalCompetitorId,
			String teamName, List<JsonImportMember> members) {
	}

	private record JsonImportMember(Long externalUserId, String firstName, String lastName) {
	}

}
