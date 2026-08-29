package fi.mlappi.golf.controller;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class LeaderboardScore {
	private String name;
	private String position;
	private int total;
	private int totalAll;
	private int thru;
	private int roundsPlayed;
	private int skinsWon;
	private double grossTotal;
	private double stakeTotal;
	private double netTotal;
	private List<Integer> score = new ArrayList<>();

	public double getAverageToPar() {
		return roundsPlayed == 0 ? 0d : (double) total / roundsPlayed;
	}
	
}
