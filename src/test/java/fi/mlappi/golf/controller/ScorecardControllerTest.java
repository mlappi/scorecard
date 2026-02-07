package fi.mlappi.golf.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import fi.mlappi.golf.model.Course;
import fi.mlappi.golf.model.Game;
import fi.mlappi.golf.model.Player;
import fi.mlappi.golf.model.Round;
import fi.mlappi.golf.model.Scorecard;
import fi.mlappi.golf.service.GameService;
import fi.mlappi.golf.service.PlayerService;
import fi.mlappi.golf.service.ScorecardService;

@ExtendWith(MockitoExtension.class)
class ScorecardControllerTest {

    @Mock
    private GameService gameService;

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

        String view = controller.save(model, score, 3L, null, 2L, result);

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

        String view = controller.save(model, score, 3L, 8L, 2L, result);

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

        String view = controller.save(model, score, 3L, 8L, 2L, result);

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
    void leaderboardDefaultsToTotalSort() {
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

        String view = controller.leaderboard(model, 1L, "unknown");

        assertThat(view).isEqualTo("leaderboard");
        assertThat(model.get("sort")).isEqualTo("total");
        assertThat(model.get("scores")).isInstanceOf(List.class);
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
