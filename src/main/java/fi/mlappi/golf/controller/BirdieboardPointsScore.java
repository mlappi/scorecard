package fi.mlappi.golf.controller;

import lombok.Data;

@Data
public class BirdieboardPointsScore {
	private String name;
	private int birdies;
	private int eagles;

	public int getPoints() {
		return birdies + eagles * 2;
	}
}
