package fi.mlappi.golf.model;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author mlappi
 */
public class Scorecard {

    private long id;
    private long gameId;
    private long playerId;
    private String player;
    private Set<Integer> winners = new HashSet<>();

    private int hole1;
    private int hole2;
    private int hole3;
    private int hole4;
    private int hole5;
    private int hole6;
    private int hole7;
    private int hole8;
    private int hole9;

    private int hole10;
    private int hole11;
    private int hole12;
    private int hole13;
    private int hole14;
    private int hole15;
    private int hole16;
    private int hole17;
    private int hole18;
    
    private int in;
    private int out;
    private int total;
    private double win;

    private static final AtomicLong counter = new AtomicLong(100);

    public Scorecard(long gameId, long playerID) {    	
		this.gameId = gameId;
		this.playerId = playerID;		
		this.id = counter.incrementAndGet();
	}

    public Scorecard(long id, long gameId, long playerID) {
    	this.id = id;
		this.gameId = gameId;
		this.playerId = playerID;		
	}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

	public long getGameId() {
		return gameId;
	}

	public void setGameId(long gameId) {
		this.gameId = gameId;
	}

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public int getHole1() {
		return hole1;
	}

	public void setHole1(int hole1) {
		this.hole1 = hole1;
	}

	public int getHole2() {
		return hole2;
	}

	public void setHole2(int hole2) {
		this.hole2 = hole2;
	}

	public int getHole3() {
		return hole3;
	}

	public void setHole3(int hole3) {
		this.hole3 = hole3;
	}

	public int getHole4() {
		return hole4;
	}

	public void setHole4(int hole4) {
		this.hole4 = hole4;
	}

	public int getHole5() {
		return hole5;
	}

	public void setHole5(int hole5) {
		this.hole5 = hole5;
	}

	public int getHole6() {
		return hole6;
	}

	public void setHole6(int hole6) {
		this.hole6 = hole6;
	}

	public int getHole7() {
		return hole7;
	}

	public void setHole7(int hole7) {
		this.hole7 = hole7;
	}

	public int getHole8() {
		return hole8;
	}

	public void setHole8(int hole8) {
		this.hole8 = hole8;
	}

	public int getHole9() {
		return hole9;
	}

	public void setHole9(int hole9) {
		this.hole9 = hole9;
	}

	public int getHole10() {
		return hole10;
	}

	public void setHole10(int hole10) {
		this.hole10 = hole10;
	}

	public int getHole11() {
		return hole11;
	}

	public void setHole11(int hole11) {
		this.hole11 = hole11;
	}

	public int getHole12() {
		return hole12;
	}

	public void setHole12(int hole12) {
		this.hole12 = hole12;
	}

	public int getHole13() {
		return hole13;
	}

	public void setHole13(int hole13) {
		this.hole13 = hole13;
	}

	public int getHole14() {
		return hole14;
	}

	public void setHole14(int hole14) {
		this.hole14 = hole14;
	}

	public int getHole15() {
		return hole15;
	}

	public void setHole15(int hole15) {
		this.hole15 = hole15;
	}

	public int getHole16() {
		return hole16;
	}

	public void setHole16(int hole16) {
		this.hole16 = hole16;
	}

	public int getHole17() {
		return hole17;
	}

	public void setHole17(int hole17) {
		this.hole17 = hole17;
	}

	public int getHole18() {
		return hole18;
	}

	public void setHole18(int hole18) {
		this.hole18 = hole18;
	}

	public int getIn() {
		return in;
	}

	public void setIn(int in) {
		this.in = in;
	}

	public int getOut() {
		return out;
	}

	public void setOut(int out) {
		this.out = out;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public double getWin() {
		return win;
	}

	public void setWin(double win) {
		this.win = win;
	}

	public String getPlayer() {
		return player;
	}

	public void setPlayer(String player) {
		this.player = player;
	}
	
	public boolean isWinner(int hole) {
		return winners.contains(hole);
	}
	
	public Set<Integer> getWinners() {
		return winners;
	}

	public void setWinners(Set<Integer> winners) {
		this.winners = winners;
	}

	public int getScore(int hole) {
		switch (hole) {
		case 1:
			return this.hole1;
		case 2:
			return this.hole2;
		case 3:
			return this.hole3;
		case 4:
			return this.hole4;
		case 5:
			return this.hole5;
		case 6:
			return this.hole6;
		case 7:
			return this.hole7;
		case 8:
			return this.hole8;
		case 9:
			return this.hole9;
		case 10:
			return this.hole10;
		case 11:
			return this.hole11;
		case 12:
			return this.hole12;			
		case 13:
			return this.hole13;
		case 14:
			return this.hole14;
		case 15:
			return this.hole15;
		case 16:
			return this.hole16;
		case 17:
			return this.hole17;
		case 18:
			return this.hole18;			
		default:
			return 0;
		}
	}
    
}
