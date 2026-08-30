package fi.mlappi.golf.service;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class GameStatistics {

    private int competitionRounds;
    private int playerPerformances;
    private int completePerformances;
    private int uniquePlayers;
    private int playersAcrossRounds;
    private long totalStrokes;
    private long totalToPar;
    private int totalBirdies;
    private int totalEagles;
    private int fieldBirdies;
    private int fieldEagles;
    private List<RoundStatistics> rounds = new ArrayList<>();
    private List<BestCourseScore> bestCourseScores = new ArrayList<>();

    public double getAveragePlayersPerRound() {
        return divide(playersAcrossRounds, competitionRounds);
    }

    public double getAverageScore() {
        return divide(totalStrokes, completePerformances);
    }

    public double getAverageToPar() {
        return divide(totalToPar, completePerformances);
    }

    public double getAverageBirdiesPerPerformance() {
        return divide(totalBirdies, completePerformances);
    }

    public double getAverageEaglesPerPerformance() {
        return divide(totalEagles, completePerformances);
    }

    public double getAverageFieldBirdiesPerRound() {
        return divide(fieldBirdies, competitionRounds);
    }

    public double getAverageFieldEaglesPerRound() {
        return divide(fieldEagles, competitionRounds);
    }

    private double divide(long value, int divisor) {
        return divisor == 0 ? 0d : (double) value / divisor;
    }
}
