package fi.mlappi.golf.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;

import lombok.Data;

/**
 *
 * @author mlappi
 */
@Entity
@Data
public class Scorecard {

	@Id
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "sequence", allocationSize = 10)
    private Long id;
    @OneToOne(fetch=FetchType.EAGER)
    private Round round;
    @OneToOne(fetch=FetchType.EAGER)
    private Player player;
    private Integer hole1;
    private Integer hole2;
    private Integer hole3;
    private Integer hole4;
    private Integer hole5;
    private Integer hole6;
    private Integer hole7;
    private Integer hole8;
    private Integer hole9;
    private Integer hole10;
    private Integer hole11;
    private Integer hole12;
    private Integer hole13;
    private Integer hole14;
    private Integer hole15;
    private Integer hole16;
    private Integer hole17;
    private Integer hole18;    
    @Transient
    private double win;
    
    @Transient
    private Set<Integer> winners = new HashSet<>();


	public Integer getScore(int hole) {
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
	
	
	public int getCountTotal() {
		return getCountOut() + getCountIn();
	}
	
	public int getCountOut() {
		int sum = (getHole1() != null ? getHole1() : Integer.valueOf(0)) +
				(getHole2() != null ? getHole2() : 0) +
				(getHole3() != null ? getHole3() : 0) +
				(getHole4() != null ? getHole4() : 0) +
				(getHole5() != null ? getHole5() : 0) +
				(getHole6() != null ? getHole6() : 0) +
				(getHole7() != null ? getHole7() : 0) +
				(getHole8() != null ? getHole8() : 0) +
				(getHole9() != null ? getHole9() : 0);
		return sum;
	}

	public int getCountIn() {
		int sum = (getHole10() != null ? getHole10() : Integer.valueOf(0)) +
				(getHole11() != null ? getHole11() : 0) +
				(getHole12() != null ? getHole12() : 0) +
				(getHole13() != null ? getHole13() : 0) +
				(getHole14() != null ? getHole14() : 0) +
				(getHole15() != null ? getHole15() : 0) +
				(getHole16() != null ? getHole16() : 0) +
				(getHole17() != null ? getHole17() : 0) +
				(getHole18() != null ? getHole18() : 0);				
		return sum;
	}

	public boolean isWinner(int hole) {
		return winners.contains(hole);
	}
    
}
