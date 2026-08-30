package fi.mlappi.golf.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fi.mlappi.golf.model.Course;
import fi.mlappi.golf.model.Hole;
import fi.mlappi.golf.model.PlayFormat;
import fi.mlappi.golf.model.Player;
import fi.mlappi.golf.model.Round;
import fi.mlappi.golf.model.Scorecard;
import fi.mlappi.golf.repository.RoundRepository;
import fi.mlappi.golf.repository.ScorecardRepository;

@ExtendWith(MockitoExtension.class)
class ScorecardServiceTest {

    @Mock
    private ScorecardRepository scorecardRepository;

    @Mock
    private RoundRepository roundRepository;

    @InjectMocks
    private ScorecardService service;

    @Test
    void teamScorecardCompetesOnceAndCreditsWinningSkinsToTheCard() {
        Round round = buildRound();
        Scorecard winningTeam = buildTeamScorecard(1L, round, 3, 10L, 11L);
        Scorecard otherTeam = buildTeamScorecard(2L, round, 4, 12L, 13L);
        when(roundRepository.findById(5L)).thenReturn(java.util.Optional.of(round));
        when(scorecardRepository.findByRoundId(5L)).thenReturn(List.of(winningTeam, otherTeam));

        List<Scorecard> scores = service.countWins(5L);

        assertThat(scores).hasSize(2);
        assertThat(winningTeam.getSkinsWon()).isEqualTo(18);
        assertThat(winningTeam.getWin()).isEqualTo(36d);
        assertThat(otherTeam.getSkinsWon()).isZero();
        assertThat(otherTeam.getWin()).isZero();
    }

    @Test
    void completeCardHelpersUseAllEighteenHolesAndCoursePar() {
        Round round = buildRound();
        Scorecard scorecard = buildTeamScorecard(1L, round, 4, 10L, 11L);
        scorecard.setHole1(3);
        scorecard.setHole2(2);

        assertThat(service.hasCompleteScore(scorecard)).isTrue();
        assertThat(service.getScoreToPar(scorecard)).isEqualTo(-3);
        assertThat(service.countScoresRelativeToPar(scorecard, -1)).isEqualTo(1);
        assertThat(service.countScoresRelativeToPar(scorecard, -2)).isEqualTo(1);

        scorecard.setHole18(null);

        assertThat(service.hasCompleteScore(scorecard)).isFalse();
        assertThat(service.getScoreToPar(scorecard)).isZero();
        assertThat(service.countScoresRelativeToPar(scorecard, -1)).isZero();
    }

    private Round buildRound() {
        Course course = new Course();
        for (int number = 1; number <= 18; number++) {
            Hole hole = new Hole();
            hole.setHole(number);
            hole.setPar(4);
            hole.setCourse(course);
            course.getHole().add(hole);
        }
        Round round = new Round();
        round.setId(5L);
        round.setCourse(course);
        round.setBet(1d);
        round.setBasicSkin(true);
        return round;
    }

    private Scorecard buildTeamScorecard(Long id, Round round, int strokes, Long firstId, Long secondId) {
        Scorecard scorecard = new Scorecard();
        scorecard.setId(id);
        scorecard.setRound(round);
        scorecard.setPlayFormat(PlayFormat.GREENSOME);
        scorecard.setExternalCompetitorId("team-" + id);
        Player first = new Player();
        first.setId(firstId);
        Player second = new Player();
        second.setId(secondId);
        scorecard.getParticipants().add(first);
        scorecard.getParticipants().add(second);
        scorecard.setHole1(strokes); scorecard.setHole2(strokes); scorecard.setHole3(strokes);
        scorecard.setHole4(strokes); scorecard.setHole5(strokes); scorecard.setHole6(strokes);
        scorecard.setHole7(strokes); scorecard.setHole8(strokes); scorecard.setHole9(strokes);
        scorecard.setHole10(strokes); scorecard.setHole11(strokes); scorecard.setHole12(strokes);
        scorecard.setHole13(strokes); scorecard.setHole14(strokes); scorecard.setHole15(strokes);
        scorecard.setHole16(strokes); scorecard.setHole17(strokes); scorecard.setHole18(strokes);
        return scorecard;
    }
}
