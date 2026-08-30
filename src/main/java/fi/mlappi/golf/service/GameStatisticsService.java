package fi.mlappi.golf.service;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import fi.mlappi.golf.model.Game;
import fi.mlappi.golf.model.Player;
import fi.mlappi.golf.model.Round;
import fi.mlappi.golf.model.Scorecard;

@Service
public class GameStatisticsService {

    private final ScorecardService scorecardService;

    public GameStatisticsService(ScorecardService scorecardService) {
        this.scorecardService = scorecardService;
    }

    public GameStatistics calculate(Game game) {
        GameStatistics statistics = new GameStatistics();
        if (game == null || game.getRound() == null) {
            return statistics;
        }

        Set<Long> gamePlayerIds = new HashSet<>();
        Map<String, BestCourseScore> bestScoresByCourse = new LinkedHashMap<>();
        for (Round round : game.getRound()) {
            List<Scorecard> scorecards = scorecardService.findByRoundId(round.getId());
            if (scorecards.isEmpty()) {
                continue;
            }
            updateBestCourseScores(bestScoresByCourse, round, scorecards);
            RoundStatistics roundStatistics = calculateRound(round, scorecards, gamePlayerIds);
            statistics.getRounds().add(roundStatistics);
            statistics.setCompetitionRounds(statistics.getCompetitionRounds() + 1);
            statistics.setPlayerPerformances(
                    statistics.getPlayerPerformances() + roundStatistics.getPlayerPerformances());
            statistics.setCompletePerformances(
                    statistics.getCompletePerformances() + roundStatistics.getCompletePerformances());
            statistics.setPlayersAcrossRounds(
                    statistics.getPlayersAcrossRounds() + roundStatistics.getUniquePlayers());
            statistics.setTotalStrokes(statistics.getTotalStrokes() + roundStatistics.getTotalStrokes());
            statistics.setTotalToPar(statistics.getTotalToPar() + roundStatistics.getTotalToPar());
            statistics.setTotalBirdies(statistics.getTotalBirdies() + roundStatistics.getTotalBirdies());
            statistics.setTotalEagles(statistics.getTotalEagles() + roundStatistics.getTotalEagles());
            statistics.setFieldBirdies(statistics.getFieldBirdies() + roundStatistics.getFieldBirdies());
            statistics.setFieldEagles(statistics.getFieldEagles() + roundStatistics.getFieldEagles());
        }
        statistics.setUniquePlayers(gamePlayerIds.size());
        statistics.getBestCourseScores().addAll(bestScoresByCourse.values());
        return statistics;
    }

    private void updateBestCourseScores(Map<String, BestCourseScore> bestScoresByCourse, Round round,
            List<Scorecard> scorecards) {
        if (round.getCourse() == null) {
            return;
        }
        String courseName = round.getCourse().getName();
        String courseKey = round.getCourse().getId() == null
                ? "name:" + courseName
                : "id:" + round.getCourse().getId();
        for (Scorecard scorecard : scorecards) {
            if (scorecard.getCompetitors().isEmpty() || !scorecardService.hasCompleteScore(scorecard)) {
                continue;
            }
            int toPar = scorecardService.getScoreToPar(scorecard);
            BestCourseScore current = bestScoresByCourse.get(courseKey);
            if (current == null || toPar < current.getToPar()) {
                BestCourseScore best = new BestCourseScore();
                best.setCourseName(courseName);
                best.setStrokes(scorecard.getCountTotal());
                best.setToPar(toPar);
                addCompetitor(best, scorecard.getDisplayName());
                bestScoresByCourse.put(courseKey, best);
            } else if (toPar == current.getToPar()) {
                addCompetitor(current, scorecard.getDisplayName());
            }
        }
    }

    private void addCompetitor(BestCourseScore score, String competitor) {
        if (competitor != null && !competitor.isBlank() && !score.getCompetitors().contains(competitor)) {
            score.getCompetitors().add(competitor);
        }
    }

    private RoundStatistics calculateRound(Round round, List<Scorecard> scorecards, Set<Long> gamePlayerIds) {
        RoundStatistics statistics = new RoundStatistics();
        statistics.setRound(round);
        Set<Long> roundPlayerIds = new HashSet<>();

        for (Scorecard scorecard : scorecards) {
            List<Player> competitors = scorecard.getCompetitors();
            int performanceCount = competitors.size();
            statistics.setPlayerPerformances(statistics.getPlayerPerformances() + performanceCount);
            for (Player player : competitors) {
                if (player.getId() != null) {
                    roundPlayerIds.add(player.getId());
                    gamePlayerIds.add(player.getId());
                }
            }
            if (performanceCount == 0 || !scorecardService.hasCompleteScore(scorecard)) {
                continue;
            }

            int total = scorecard.getCountTotal();
            int toPar = scorecardService.getScoreToPar(scorecard);
            int birdies = scorecardService.countScoresRelativeToPar(scorecard, -1);
            int eagles = scorecardService.countScoresRelativeToPar(scorecard, -2);
            statistics.setCompletePerformances(statistics.getCompletePerformances() + performanceCount);
            statistics.setTotalStrokes(statistics.getTotalStrokes() + (long) total * performanceCount);
            statistics.setTotalToPar(statistics.getTotalToPar() + (long) toPar * performanceCount);
            statistics.setTotalBirdies(statistics.getTotalBirdies() + birdies * performanceCount);
            statistics.setTotalEagles(statistics.getTotalEagles() + eagles * performanceCount);
            statistics.setFieldBirdies(statistics.getFieldBirdies() + birdies);
            statistics.setFieldEagles(statistics.getFieldEagles() + eagles);
        }

        statistics.setUniquePlayers(roundPlayerIds.size());
        return statistics;
    }
}
