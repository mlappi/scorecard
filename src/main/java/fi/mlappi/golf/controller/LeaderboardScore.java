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
	private List<Integer> score = new ArrayList<>();
	
}
