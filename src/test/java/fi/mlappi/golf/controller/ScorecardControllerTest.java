package fi.mlappi.golf.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import fi.mlappi.golf.model.Course;
import fi.mlappi.golf.model.Game;
import fi.mlappi.golf.model.Hole;
import fi.mlappi.golf.model.Player;
import fi.mlappi.golf.model.PlayFormat;
import fi.mlappi.golf.model.Round;
import fi.mlappi.golf.model.Scorecard;
import fi.mlappi.golf.service.GameService;
import fi.mlappi.golf.service.GameStatistics;
import fi.mlappi.golf.service.GameStatisticsService;
import fi.mlappi.golf.service.PlayerService;
import fi.mlappi.golf.service.ScorecardService;

@ExtendWith(MockitoExtension.class)
class ScorecardControllerTest {

    @Mock
    private GameService gameService;

    @Mock
    private GameStatisticsService gameStatisticsService;

    @Mock
    private ScorecardService scoreService;

    @Mock
    private PlayerService playerService;

    @InjectMocks
    private ScorecardController controller;

    @Test
    void addInitializesScorecardAndModel() {
        Round round = buildRound(2L, 3L);
        when(gameService.findRound(2L)).thenReturn(round);
        when(scoreService.findByRoundId(2L)).thenReturn(Collections.emptyList());
        when(playerService.getAllPlayers()).thenReturn(Collections.emptyList());
        ModelMap model = new ModelMap();

        String view = controller.add(model, 2L);

        assertThat(view).isEqualTo("new-scorecard");
        assertThat(model.get("roundId")).isEqualTo(2L);
        assertThat(model.get("gameId")).isEqualTo(3L);
    }

    @Test
    void editLoadsExistingScorecard() {
        Round round = buildRound(2L, 3L);
        Scorecard scorecard = new Scorecard();
        scorecard.setRound(round);
        when(scoreService.find(5L)).thenReturn(scorecard);
        when(scoreService.findByRoundId(2L)).thenReturn(Collections.emptyList());
        when(playerService.getAllPlayers()).thenReturn(Collections.emptyList());
        ModelMap model = new ModelMap();

        String view = controller.edit(model, 5L);

        assertThat(view).isEqualTo("new-scorecard");
        assertThat(model.get("score")).isSameAs(scorecard);
    }

    @Test
    void scoreListDefaultsToFirstRoundAndSortsByWin() {
        Game game = buildGameWithRounds();
        when(gameService.find(1L)).thenReturn(game);
        when(scoreService.countWins(10L)).thenReturn(Collections.emptyList());
        when(scoreService.findByRoundId(10L)).thenReturn(Collections.emptyList());
        when(playerService.getAllPlayers()).thenReturn(Collections.emptyList());
        RoundSelect select = new RoundSelect();
        ModelMap model = new ModelMap();

        String view = controller.scoreList(model, 1L, select, null);

        assertThat(view).isEqualTo("list-scores");
        assertThat(select.getRoundId()).isEqualTo(10L);
        assertThat(model.get("sort")).isEqualTo("win");
    }

    @Test
    void saveRejectsMissingPlayer() {
        Round round = buildRound(2L, 3L);
        Scorecard score = new Scorecard();
        when(gameService.findRound(2L)).thenReturn(round);
        when(scoreService.findByRoundId(2L)).thenReturn(Collections.emptyList());
        when(playerService.getAllPlayers()).thenReturn(Collections.emptyList());
        ModelMap model = new ModelMap();
        BindingResult result = new BeanPropertyBindingResult(score, "score");

        String view = controller.save(model, score, 3L, null, 2L, null, null, null, result);

        assertThat(view).isEqualTo("new-scorecard");
        verify(scoreService, never()).save(any(Scorecard.class));
    }

    @Test
    void saveRejectsMissingHoles() {
        Round round = buildRound(2L, 3L);
        Player player = new Player();
        player.setId(8L);
        Scorecard score = new Scorecard();
        score.setPlayer(player);
        when(gameService.findRound(2L)).thenReturn(round);
        when(playerService.find(8L)).thenReturn(player);
        when(scoreService.findByRoundId(2L)).thenReturn(Collections.emptyList());
        when(playerService.getAllPlayers()).thenReturn(Collections.singletonList(player));
        ModelMap model = new ModelMap();
        BindingResult result = new BeanPropertyBindingResult(score, "score");

        String view = controller.save(model, score, 3L, 8L, 2L, null, null, null, result);

        assertThat(view).isEqualTo("new-scorecard");
        verify(scoreService, never()).save(any(Scorecard.class));
    }

    @Test
    void savePersistsScorecardWhenValid() {
        Round round = buildRound(2L, 3L);
        Player player = new Player();
        player.setId(8L);
        Scorecard score = buildCompleteScorecard();
        score.setPlayer(player);
        when(gameService.findRound(2L)).thenReturn(round);
        when(playerService.find(8L)).thenReturn(player);
        ModelMap model = new ModelMap();
        BindingResult result = new BeanPropertyBindingResult(score, "score");

        String view = controller.save(model, score, 3L, 8L, 2L, null, null, null, result);

        assertThat(view).isEqualTo("redirect:/score/list/3?roundId=2");
        verify(scoreService).save(score);
    }

    @Test
    void importScoresRejectsInvalidPlayer() {
        Round round = buildRound(2L, 3L);
        when(gameService.findRound(2L)).thenReturn(round);
        when(scoreService.findByRoundId(2L)).thenReturn(Collections.emptyList());
        when(playerService.getAllPlayers()).thenReturn(Collections.emptyList());
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.importScores(3L, 2L, 99L, "1 2 3", redirectAttributes);

        assertThat(view).isEqualTo("redirect:/score/list/3");
        assertThat(redirectAttributes.getFlashAttributes()).containsKey("error");
        verify(scoreService, never()).save(any(Scorecard.class));
    }

    @Test
    void importScoresRejectsInvalidTotalsForTwentyOneNumbers() {
        Round round = buildRound(2L, 3L);
        Player player = new Player();
        player.setId(7L);
        when(gameService.findRound(2L)).thenReturn(round);
        when(scoreService.findByRoundId(2L)).thenReturn(Collections.emptyList());
        when(playerService.getAllPlayers()).thenReturn(Collections.singletonList(player));
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.importScores(3L, 2L, 7L,
                "1 1 1 1 1 1 1 1 1 10 1 1 1 1 1 1 1 1 1 10 25",
                redirectAttributes);

        assertThat(view).isEqualTo("redirect:/score/list/3");
        assertThat(redirectAttributes.getFlashAttributes()).containsKey("error");
        verify(scoreService, never()).save(any(Scorecard.class));
    }

    @Test
    void importScoresSavesValidEighteenNumbers() {
        Round round = buildRound(2L, 3L);
        Player player = new Player();
        player.setId(7L);
        player.setFirstName("Matti");
        player.setLastName("Mallikas");
        when(gameService.findRound(2L)).thenReturn(round);
        when(scoreService.findByRoundId(2L)).thenReturn(Collections.emptyList());
        when(playerService.getAllPlayers()).thenReturn(Collections.singletonList(player));
        when(playerService.find(7L)).thenReturn(player);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.importScores(3L, 2L, 7L,
                "1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18",
                redirectAttributes);

        assertThat(view).isEqualTo("redirect:/score/list/3?roundId=2");
        assertThat(redirectAttributes.getFlashAttributes()).containsKey("message");
        verify(scoreService).save(any(Scorecard.class));
    }

    @Test
    void importScoresFromJsonSavesTwoRoundsAndConvertsDashToParPlusFive() {
        Round firstRound = buildRound(10L, 3L);
        Round secondRound = buildRound(11L, 3L);
        addParHoles(firstRound.getCourse(), 4);
        addParHoles(secondRound.getCourse(), 3);
        Player player = buildPlayer(7L, "Matti", "Mallikas");
        controller.objectMapper = new ObjectMapper();
        when(gameService.findRound(10L)).thenReturn(firstRound);
        when(gameService.findRound(11L)).thenReturn(secondRound);
        when(playerService.findOrCreateByExternalUserId(123L, "Matti", "Mallikas", -1.2)).thenReturn(player);
        MockMultipartFile file = new MockMultipartFile("resultsFile", "results.json", "application/json",
                """
                {"data":{"primary":{"players":[{"name":"Matti Mallikas","userId":123,"rounds":[
                {"hcp":-1.2,"strokes":["4","-","4","4","4","4","4","4","4","4","4","4","4","4","4","4","4","4"]},
                {"hcp":-1.2,"strokes":["3","-","3","3","3","3","3","3","3","3","3","3","3","3","3","3","3","3"]}
                ]}]}}}
                """.getBytes());
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.importScoresFromJson(3L, null, 10L, 11L, file, redirectAttributes);

        assertThat(view).isEqualTo("redirect:/score/list/3?roundId=10");
        assertThat(redirectAttributes.getFlashAttributes().get("message")).isEqualTo(
                "Tuotu 2 tuloskorttia. Ohitettu 0 riviä.");
        ArgumentCaptor<Scorecard> captor = ArgumentCaptor.forClass(Scorecard.class);
        verify(scoreService, times(2)).save(captor.capture());
        assertThat(captor.getAllValues().get(0).getRound()).isSameAs(firstRound);
        assertThat(captor.getAllValues().get(0).getHole2()).isEqualTo(9);
        assertThat(captor.getAllValues().get(1).getRound()).isSameAs(secondRound);
        assertThat(captor.getAllValues().get(1).getHole2()).isEqualTo(8);
    }

    @Test
    void importScoresFromJsonImportsGreensomeTeam() {
        Round round = buildRound(10L, 3L);
        addParHoles(round.getCourse(), 4);
        Player firstMember = buildPlayer(7L, "Kari", "Kärkkäinen");
        Player secondMember = buildPlayer(8L, "Tommi", "Mustikkamaa");
        controller.objectMapper = new ObjectMapper();
        when(gameService.findRound(10L)).thenReturn(round);
        when(playerService.findOrCreateByExternalUserId(7631L, "Kari", "Kärkkäinen", null)).thenReturn(firstMember);
        when(playerService.findOrCreateByExternalUserId(579284L, "Tommi", "Mustikkamaa", null)).thenReturn(secondMember);
        MockMultipartFile file = new MockMultipartFile("resultsFile", "greensome.json", "application/json",
                """
                {"data":{"primary":{"players":[{
                  "name":"Team KaTo","teamId":"176732763",
                  "team":[{"name":"Kari Kärkkäinen","userId":7631},{"name":"Tommi Mustikkamaa","userId":579284}],
                  "rounds":[{"playingHcp":-17,"strokes":["7","4","4","5","3","6","5","3","4","4","6","5","3","4","3","6","5","5"]}]
                }]}}}
                """.getBytes());
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.importScoresFromJson(3L, null, 10L, null, file, redirectAttributes);

        assertThat(view).isEqualTo("redirect:/score/list/3?roundId=10");
        assertThat(redirectAttributes.getFlashAttributes().get("message")).isEqualTo(
                "Tuotu 1 tuloskorttia. Ohitettu 0 riviä.");
        ArgumentCaptor<Scorecard> captor = ArgumentCaptor.forClass(Scorecard.class);
        verify(scoreService).save(captor.capture());
        assertThat(captor.getValue().getPlayer()).isNull();
        assertThat(captor.getValue().getPlayFormat()).isEqualTo(PlayFormat.GREENSOME);
        assertThat(captor.getValue().getExternalCompetitorId()).isEqualTo("176732763");
        assertThat(captor.getValue().getTeamName()).isEqualTo("Team KaTo");
        assertThat(captor.getValue().getPlayingHcp()).isEqualTo(-17d);
        assertThat(captor.getValue().getParticipants()).containsExactlyInAnyOrder(firstMember, secondMember);
        assertThat(captor.getValue().getHole1()).isEqualTo(7);
        assertThat(captor.getValue().getHole18()).isEqualTo(5);
    }

    @Test
    void leaderboardDefaultsToNetTotalSort() {
        Game game = buildGameWithRounds();
        Scorecard scorecard = buildCompleteScorecard();
        Player player = new Player();
        player.setId(5L);
        player.setFirstName("Ari");
        player.setLastName("Aalto");
        scorecard.setPlayer(player);
        scorecard.setRound(game.getRound().get(0));
        when(gameService.find(1L)).thenReturn(game);
        when(scoreService.countWins(10L)).thenReturn(List.of(scorecard));
        when(scoreService.countWins(11L)).thenReturn(Collections.emptyList());
        ModelMap model = new ModelMap();

        String view = controller.leaderboard(model, 1L, "unknown", null);

        assertThat(view).isEqualTo("leaderboard");
        assertThat(model.get("sort")).isEqualTo("netTotal");
        assertThat(model.get("view")).isEqualTo("money");
        assertThat(model.get("scores")).isInstanceOf(List.class);
    }

    @Test
    void leaderboardLegacyStrokeSortSelectsStrokeView() {
        Game game = buildGameWithRounds();
        Scorecard scorecard = buildCompleteScorecard();
        scorecard.setPlayer(buildPlayer(5L, "Ari", "Aalto"));
        scorecard.setRound(game.getRound().get(0));
        when(gameService.find(1L)).thenReturn(game);
        when(scoreService.countWins(10L)).thenReturn(List.of(scorecard));
        when(scoreService.countWins(11L)).thenReturn(Collections.emptyList());
        ModelMap model = new ModelMap();

        controller.leaderboard(model, 1L, "total", null);

        assertThat(model.get("view")).isEqualTo("stroke");
        assertThat(model.get("sort")).isEqualTo("total");
    }

    @Test
    void leaderboardCreditsTeamSkinsToMembersAndSplitsNetIncome() {
        Game game = buildGameWithRounds();
        Round round = game.getRound().get(0);
        Player firstPlayer = buildPlayer(5L, "Ari", "Aalto");
        Player secondPlayer = buildPlayer(6L, "Bertta", "Birdie");
        Scorecard teamScore = buildCompleteScorecard();
        teamScore.setRound(round);
        teamScore.setPlayFormat(PlayFormat.GREENSOME);
        teamScore.setSkinsWon(3);
        teamScore.setWin(10d);
        teamScore.getParticipants().add(firstPlayer);
        teamScore.getParticipants().add(secondPlayer);
        when(gameService.find(1L)).thenReturn(game);
        when(scoreService.countWins(10L)).thenReturn(List.of(teamScore));
        when(scoreService.countWins(11L)).thenReturn(Collections.emptyList());
        when(scoreService.getPlayerStake(round, List.of(teamScore))).thenReturn(2d);
        ModelMap model = new ModelMap();

        controller.leaderboard(model, 1L, null, null);

        List<LeaderboardScore> scores = (List<LeaderboardScore>) model.get("scores");
        assertThat(scores).hasSize(2);
        assertThat(scores).allSatisfy(score -> {
            assertThat(score.getSkinsWon()).isEqualTo(3);
            assertThat(score.getRoundsPlayed()).isEqualTo(1);
            assertThat(score.getGrossTotal()).isEqualTo(5d);
            assertThat(score.getStakeTotal()).isEqualTo(1d);
            assertThat(score.getNetTotal()).isEqualTo(4d);
            assertThat(score.getTotalAll()).isEqualTo(72);
            assertThat(score.getScore().get(0)).isEqualTo(72);
        });
        assertThat((List<Long>) model.get("rounds")).containsExactly(10L, 11L);
        assertThat((Set<Long>) model.get("teamRounds")).containsExactly(10L);
    }

    @Test
    void moneyLeaderboardShowsWinnerAndLoserNetResults() {
        Game game = buildGameWithRounds();
        Round round = game.getRound().get(0);
        Scorecard winner = buildCompleteScorecard();
        winner.setRound(round);
        winner.setPlayer(buildPlayer(5L, "Ari", "Aalto"));
        winner.setWin(6d);
        Scorecard loser = buildCompleteScorecard();
        loser.setRound(round);
        loser.setPlayer(buildPlayer(6L, "Bertta", "Birdie"));
        when(gameService.find(1L)).thenReturn(game);
        when(scoreService.countWins(10L)).thenReturn(List.of(winner, loser));
        when(scoreService.countWins(11L)).thenReturn(Collections.emptyList());
        when(scoreService.getPlayerStake(round, List.of(winner, loser))).thenReturn(2d);
        ModelMap model = new ModelMap();

        controller.leaderboard(model, 1L, null, "money");

        List<LeaderboardScore> scores = (List<LeaderboardScore>) model.get("scores");
        assertThat(scores.get(0).getName()).isEqualTo("Ari Aalto");
        assertThat(scores.get(0).getGrossTotal()).isEqualTo(6d);
        assertThat(scores.get(0).getStakeTotal()).isEqualTo(2d);
        assertThat(scores.get(0).getNetTotal()).isEqualTo(4d);
        assertThat(scores.get(1).getName()).isEqualTo("Bertta Birdie");
        assertThat(scores.get(1).getNetTotal()).isEqualTo(-2d);
    }

    @Test
    void strokeLeaderboardSortsAverageThenMoreRounds() {
        Game game = buildGameWithRounds();
        Round firstRound = game.getRound().get(0);
        Round secondRound = game.getRound().get(1);
        addParHoles(firstRound.getCourse(), 4);
        addParHoles(secondRound.getCourse(), 4);
        Player regular = buildPlayer(5L, "Ari", "Aalto");
        Player oneRound = buildPlayer(6L, "Bertta", "Birdie");
        Scorecard regularFirst = buildCompleteScorecard();
        regularFirst.setRound(firstRound);
        regularFirst.setPlayer(regular);
        Scorecard regularSecond = buildCompleteScorecard();
        regularSecond.setRound(secondRound);
        regularSecond.setPlayer(regular);
        regularSecond.setHole1(6);
        Scorecard oneRoundScore = buildCompleteScorecard();
        oneRoundScore.setRound(firstRound);
        oneRoundScore.setPlayer(oneRound);
        oneRoundScore.setHole1(5);
        when(gameService.find(1L)).thenReturn(game);
        when(scoreService.countWins(10L)).thenReturn(List.of(regularFirst, oneRoundScore));
        when(scoreService.countWins(11L)).thenReturn(List.of(regularSecond));
        ModelMap model = new ModelMap();

        controller.leaderboard(model, 1L, null, "stroke");

        List<LeaderboardScore> scores = (List<LeaderboardScore>) model.get("scores");
        assertThat(model.get("sort")).isEqualTo("average");
        assertThat(scores.get(0).getName()).isEqualTo("Ari Aalto");
        assertThat(scores.get(0).getRoundsPlayed()).isEqualTo(2);
        assertThat(scores.get(0).getAverageToPar()).isEqualTo(1d);
        assertThat(scores.get(1).getName()).isEqualTo("Bertta Birdie");
        assertThat(scores.get(1).getRoundsPlayed()).isEqualTo(1);
        assertThat(scores.get(1).getAverageToPar()).isEqualTo(1d);
    }

    @Test
    void birdieboardCountsEaglesAndBirdiesAcrossGameRounds() {
        Game game = buildGameWithRounds();
        Round round = game.getRound().get(0);
        addParFourHoles(round.getCourse());
        Player firstPlayer = buildPlayer(5L, "Ari", "Aalto");
        Player secondPlayer = buildPlayer(6L, "Bertta", "Birdie");
        Scorecard firstScore = buildCompleteScorecard();
        firstScore.setPlayer(firstPlayer);
        firstScore.setRound(round);
        firstScore.setHole1(2);
        firstScore.setHole2(3);
        firstScore.setHole3(3);
        Scorecard secondScore = buildCompleteScorecard();
        secondScore.setPlayer(secondPlayer);
        secondScore.setRound(round);
        secondScore.setHole1(3);
        Scorecard teamScore = buildCompleteScorecard();
        teamScore.setRound(round);
        teamScore.setPlayFormat(PlayFormat.GREENSOME);
        teamScore.getParticipants().add(firstPlayer);
        teamScore.getParticipants().add(secondPlayer);
        teamScore.setHole4(3);
        when(gameService.find(1L)).thenReturn(game);
        when(scoreService.findByRoundId(10L)).thenReturn(List.of(firstScore, secondScore, teamScore));
        when(scoreService.findByRoundId(11L)).thenReturn(Collections.emptyList());
        when(scoreService.countScoresRelativeToPar(firstScore, -2)).thenReturn(1);
        when(scoreService.countScoresRelativeToPar(firstScore, -1)).thenReturn(2);
        when(scoreService.countScoresRelativeToPar(secondScore, -2)).thenReturn(0);
        when(scoreService.countScoresRelativeToPar(secondScore, -1)).thenReturn(1);
        when(scoreService.countScoresRelativeToPar(teamScore, -2)).thenReturn(0);
        when(scoreService.countScoresRelativeToPar(teamScore, -1)).thenReturn(1);
        ModelMap model = new ModelMap();

        String view = controller.birdieboard(model, 1L);

        assertThat(view).isEqualTo("birdieboard");
        List<BirdieboardScore> eagles = (List<BirdieboardScore>) model.get("eagles");
        List<BirdieboardScore> birdies = (List<BirdieboardScore>) model.get("birdies");
        assertThat(eagles).hasSize(1);
        assertThat(eagles.get(0).getName()).isEqualTo("Ari Aalto");
        assertThat(eagles.get(0).getTotal()).isEqualTo(1);
        assertThat(birdies.get(0).getName()).isEqualTo("Ari Aalto");
        assertThat(birdies.get(0).getIndividualTotal()).isEqualTo(2);
        assertThat(birdies.get(0).getTeamTotal()).isEqualTo(1);
        assertThat(birdies.get(0).getTotal()).isEqualTo(3);
        assertThat(birdies.get(1).getName()).isEqualTo("Bertta Birdie");
        assertThat(birdies.get(1).getIndividualTotal()).isEqualTo(1);
        assertThat(birdies.get(1).getTeamTotal()).isEqualTo(1);
        assertThat(birdies.get(1).getTotal()).isEqualTo(2);
    }

    @Test
    void statisticsShowsCalculatedGameStatistics() {
        Game game = buildGameWithRounds();
        GameStatistics statistics = new GameStatistics();
        statistics.setCompetitionRounds(2);
        when(gameService.find(1L)).thenReturn(game);
        when(gameStatisticsService.calculate(game)).thenReturn(statistics);
        ModelMap model = new ModelMap();

        String view = controller.statistics(model, 1L);

        assertThat(view).isEqualTo("statistics");
        assertThat(model.get("gameId")).isEqualTo(1L);
        assertThat(model.get("game")).isSameAs(game);
        assertThat(model.get("statistics")).isSameAs(statistics);
    }

    private Round buildRound(long roundId, long gameId) {
        Game game = new Game();
        game.setId(gameId);
        Round round = new Round();
        round.setId(roundId);
        round.setGame(game);
        Course course = new Course();
        course.setId(1L);
        course.setName("Testikentta");
        round.setCourse(course);
        return round;
    }

    private Game buildGameWithRounds() {
        Game game = new Game();
        game.setId(1L);
        Round round1 = buildRound(10L, 1L);
        Round round2 = buildRound(11L, 1L);
        game.getRound().add(round1);
        game.getRound().add(round2);
        return game;
    }

    private Player buildPlayer(Long id, String firstName, String lastName) {
        Player player = new Player();
        player.setId(id);
        player.setFirstName(firstName);
        player.setLastName(lastName);
        return player;
    }

    private void addParFourHoles(Course course) {
        addParHoles(course, 4);
    }

    private void addParHoles(Course course, int par) {
        for (int i = 1; i <= 18; i++) {
            Hole hole = new Hole();
            hole.setHole(i);
            hole.setPar(par);
            hole.setCourse(course);
            course.getHole().add(hole);
        }
    }

    private Scorecard buildCompleteScorecard() {
        Scorecard score = new Scorecard();
        score.setHole1(4);
        score.setHole2(4);
        score.setHole3(4);
        score.setHole4(4);
        score.setHole5(4);
        score.setHole6(4);
        score.setHole7(4);
        score.setHole8(4);
        score.setHole9(4);
        score.setHole10(4);
        score.setHole11(4);
        score.setHole12(4);
        score.setHole13(4);
        score.setHole14(4);
        score.setHole15(4);
        score.setHole16(4);
        score.setHole17(4);
        score.setHole18(4);
        return score;
    }
}
