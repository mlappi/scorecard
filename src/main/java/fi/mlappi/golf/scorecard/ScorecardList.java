package fi.mlappi.golf.scorecard;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fi.mlappi.golf.model.Player;
import fi.mlappi.golf.model.Scorecard;

public class ScorecardList {
	private static final List<Scorecard> scoreList = new ArrayList<>();

	private ScorecardList() {
	}

	static {
		scoreList.add(new Scorecard(101, 101));
		scoreList.add(new Scorecard(101, 102));
		scoreList.add(new Scorecard(101, 103));
		scoreList.add(new Scorecard(101, 104));
		scoreList.add(new Scorecard(101, 105));
		scoreList.add(new Scorecard(101, 106));
		scoreList.add(new Scorecard(101, 107));
		scoreList.add(new Scorecard(101, 108));
		scoreList.add(new Scorecard(101, 109));
	}

	public static List<Scorecard> getInstance() {
		for (Scorecard scorecard : scoreList) {
			scorecard.setHole1(new Random().nextInt(5) + 4);
			scorecard.setHole2(new Random().nextInt(6) + 3);
			scorecard.setHole3(new Random().nextInt(6) + 3);
			scorecard.setHole4(new Random().nextInt(6) + 3);
			scorecard.setHole5(new Random().nextInt(6) + 3);
			scorecard.setHole6(new Random().nextInt(5) + 4);
			scorecard.setHole7(new Random().nextInt(6) + 3);
			scorecard.setHole8(new Random().nextInt(6) + 3);
			scorecard.setHole9(new Random().nextInt(6) + 3);
			scorecard.setHole10(new Random().nextInt(6) + 3);
			scorecard.setHole11(new Random().nextInt(5) + 4);
			scorecard.setHole12(new Random().nextInt(6) + 3);
			scorecard.setHole13(new Random().nextInt(6) + 3);
			scorecard.setHole14(new Random().nextInt(6) + 3);
			scorecard.setHole15(new Random().nextInt(6) + 3);
			scorecard.setHole16(new Random().nextInt(5) + 4);
			scorecard.setHole17(new Random().nextInt(6) + 3);
			scorecard.setHole18(new Random().nextInt(6) + 3);
			countOut(scorecard);
			countIn(scorecard);
			scorecard.setTotal(scorecard.getIn() + scorecard.getOut());

		}
		return scoreList;
	}

	private static void countOut(Scorecard s) {
		int sum = s.getHole1() + s.getHole2() + s.getHole3() + s.getHole4() + s.getHole5() + s.getHole6() + s.getHole7()
				+ s.getHole8() + s.getHole9();
		s.setOut(sum);
	}

	private static void countIn(Scorecard s) {
		int sum = s.getHole10() + s.getHole11() + s.getHole12() + s.getHole13() + s.getHole14() + s.getHole15()
				+ s.getHole16() + s.getHole17() + s.getHole18();
		s.setIn(sum);
	}

}
