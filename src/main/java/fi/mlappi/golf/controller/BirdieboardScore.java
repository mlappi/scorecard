package fi.mlappi.golf.controller;

import lombok.Data;

@Data
public class BirdieboardScore {
	private String name;
	private int individualTotal;
	private int teamTotal;

	public int getTotal() {
		return individualTotal + teamTotal;
	}
}
