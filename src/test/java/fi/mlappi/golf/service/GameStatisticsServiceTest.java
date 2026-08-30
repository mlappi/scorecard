package fi.mlappi.golf.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fi.mlappi.golf.model.Course;
import fi.mlappi.golf.model.Game;
import fi.mlappi.golf.model.PlayFormat;
import fi.mlappi.golf.model.Player;
import fi.mlappi.golf.model.Round;
import fi.mlappi.golf.model.Scorecard;

@ExtendWith(MockitoExtension.class)
class GameStatisticsServiceTest {

    @Mock
    private ScorecardService scorecardService;

    private GameStatisticsService service;

    @BeforeEach
    void setUp() {
        service = new GameStatisticsService(scorecardService);
    }

    @Test
    void combinesIndividualAndTeamPerformancesWithoutDuplicatingFieldEvents() {
        Game game = new Game();
        Round round = buildRound(1L, game, "Kentta");
        game.getRound().add(round);
        Player ari = buildPlayer(1L);
        Player bertta = buildPlayer(2L);
        Player cecilia = buildPlayer(3L);
        Scorecard individual = individual(round, ari);
        Scorecard team = team(round, bertta, cecilia);
        when(scorecardService.findByRoundId(1L)).thenReturn(List.of(individual, team));
        when(scorecardService.hasCompleteScore(individual)).thenReturn(true);
        when(scorecardService.hasCompleteScore(team)).thenReturn(true);
        when(scorecardService.getScoreToPar(individual)).thenReturn(-3);
        when(scorecardService.getScoreToPar(team)).thenReturn(-1);
        when(scorecardService.countScoresRelativeToPar(individual, -1)).thenReturn(1);
        when(scorecardService.countScoresRelativeToPar(individual, -2)).thenReturn(1);
        when(scorecardService.countScoresRelativeToPar(team, -1)).thenReturn(1);

        GameStatistics result = service.calculate(game);

        assertThat(result.getCompetitionRounds()).isEqualTo(1);
        assertThat(result.getPlayerPerformances()).isEqualTo(3);
        assertThat(result.getCompletePerformances()).isEqualTo(3);
        assertThat(result.getUniquePlayers()).isEqualTo(3);
        assertThat(result.getTotalStrokes()).isEqualTo(211);
        assertThat(result.getAverageScore()).isEqualTo(211d / 3d);
        assertThat(result.getTotalToPar()).isEqualTo(-5);
        assertThat(result.getTotalBirdies()).isEqualTo(3);
        assertThat(result.getTotalEagles()).isEqualTo(1);
        assertThat(result.getFieldBirdies()).isEqualTo(2);
        assertThat(result.getFieldEagles()).isEqualTo(1);
        assertThat(result.getBestCourseScores()).singleElement().satisfies(best -> {
            assertThat(best.getCourseName()).isEqualTo("Kentta");
            assertThat(best.getStrokes()).isEqualTo(69);
            assertThat(best.getToPar()).isEqualTo(-3);
        });
        assertThat(result.getRounds()).singleElement().satisfies(statistics -> {
            assertThat(statistics.getPlayerPerformances()).isEqualTo(3);
            assertThat(statistics.getAverageBirdiesPerPerformance()).isEqualTo(1d);
            assertThat(statistics.getFieldBirdies()).isEqualTo(2);
        });
    }

    @Test
    void excludesIncompleteScoresAndEmptyRoundsFromResultAverages() {
        Game game = new Game();
        Round first = buildRound(1L, game, "Ensimmainen");
        Round second = buildRound(2L, game, "Toinen");
        Round empty = buildRound(3L, game, "Tyhja");
        game.getRound().addAll(List.of(first, second, empty));
        Player returning = buildPlayer(1L);
        Player teammate = buildPlayer(2L);
        Scorecard incomplete = individual(first, returning);
        Scorecard complete = team(second, returning, teammate);
        when(scorecardService.findByRoundId(1L)).thenReturn(List.of(incomplete));
        when(scorecardService.findByRoundId(2L)).thenReturn(List.of(complete));
        when(scorecardService.findByRoundId(3L)).thenReturn(Collections.emptyList());
        when(scorecardService.hasCompleteScore(incomplete)).thenReturn(false);
        when(scorecardService.hasCompleteScore(complete)).thenReturn(true);
        when(scorecardService.getScoreToPar(complete)).thenReturn(2);

        GameStatistics result = service.calculate(game);

        assertThat(result.getCompetitionRounds()).isEqualTo(2);
        assertThat(result.getPlayerPerformances()).isEqualTo(3);
        assertThat(result.getCompletePerformances()).isEqualTo(2);
        assertThat(result.getUniquePlayers()).isEqualTo(2);
        assertThat(result.getAveragePlayersPerRound()).isEqualTo(1.5d);
        assertThat(result.getTotalStrokes()).isEqualTo(142);
        assertThat(result.getAverageScore()).isEqualTo(71d);
        assertThat(result.getTotalToPar()).isEqualTo(4);
        assertThat(result.getRounds()).hasSize(2);
        assertThat(result.getRounds().get(0).getTotalStrokes()).isZero();
    }

    @Test
    void emptyGameReturnsZeroedStatistics() {
        GameStatistics result = service.calculate(new Game());

        assertThat(result.getCompetitionRounds()).isZero();
        assertThat(result.getPlayerPerformances()).isZero();
        assertThat(result.getAveragePlayersPerRound()).isZero();
        assertThat(result.getRounds()).isEmpty();
    }

    private Round buildRound(Long id, Game game, String courseName) {
        Round round = new Round();
        round.setId(id);
        round.setGame(game);
        Course course = new Course();
        course.setName(courseName);
        round.setCourse(course);
        return round;
    }

    private Player buildPlayer(Long id) {
        Player player = new Player();
        player.setId(id);
        return player;
    }

    private Scorecard individual(Round round, Player player) {
        Scorecard scorecard = new Scorecard();
        scorecard.setRound(round);
        scorecard.setPlayer(player);
        setAllScores(scorecard, 4);
        scorecard.setHole1(1);
        return scorecard;
    }

    private Scorecard team(Round round, Player first, Player second) {
        Scorecard scorecard = new Scorecard();
        scorecard.setRound(round);
        scorecard.setPlayFormat(PlayFormat.GREENSOME);
        scorecard.getParticipants().add(first);
        scorecard.getParticipants().add(second);
        setAllScores(scorecard, 4);
        scorecard.setHole1(3);
        return scorecard;
    }

    private void setAllScores(Scorecard scorecard, int strokes) {
        scorecard.setHole1(strokes); scorecard.setHole2(strokes); scorecard.setHole3(strokes);
        scorecard.setHole4(strokes); scorecard.setHole5(strokes); scorecard.setHole6(strokes);
        scorecard.setHole7(strokes); scorecard.setHole8(strokes); scorecard.setHole9(strokes);
        scorecard.setHole10(strokes); scorecard.setHole11(strokes); scorecard.setHole12(strokes);
        scorecard.setHole13(strokes); scorecard.setHole14(strokes); scorecard.setHole15(strokes);
        scorecard.setHole16(strokes); scorecard.setHole17(strokes); scorecard.setHole18(strokes);
    }
}
