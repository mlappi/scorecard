package fi.mlappi.golf.service;

import fi.mlappi.golf.model.Round;
import lombok.Data;

@Data
public class RoundStatistics {

    private Round round;
    private int uniquePlayers;
    private int playerPerformances;
    private int completePerformances;
    private long totalStrokes;
    private long totalToPar;
    private int totalBirdies;
    private int totalEagles;
    private int fieldBirdies;
    private int fieldEagles;

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

    private double divide(long value, int divisor) {
        return divisor == 0 ? 0d : (double) value / divisor;
    }
}
