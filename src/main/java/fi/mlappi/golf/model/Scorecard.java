package fi.mlappi.golf.model;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Transient;

import lombok.Data;

/**
 *
 * @author mlappi
 */
@Entity
@Table(name = "SCORECARD", uniqueConstraints = @UniqueConstraint(columnNames = {"PLAYER_ID", "ROUND_ID"}))
@Data
public class Scorecard {

	@Id
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "sequence", allocationSize = 10)
    private Long id;
    @ManyToOne(fetch=FetchType.EAGER)
    private Round round;
    @ManyToOne(fetch=FetchType.EAGER)
    private Player player;
    @Enumerated(EnumType.STRING)
    private PlayFormat playFormat = PlayFormat.INDIVIDUAL;
    private String teamName;
    private String externalCompetitorId;
    private Double playingHcp;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "SCORECARD_PARTICIPANT",
            joinColumns = @JoinColumn(name = "SCORECARD_ID"),
            inverseJoinColumns = @JoinColumn(name = "PLAYER_ID"))
    private Set<Player> participants = new LinkedHashSet<>();
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
    private double win;
    
    @Transient
    private Set<Integer> winners = new HashSet<>();
    @Transient
    private int skinsWon;

    public PlayFormat getPlayFormat() {
        return playFormat == null ? PlayFormat.INDIVIDUAL : playFormat;
    }

    public boolean isTeamScorecard() {
        return getPlayFormat().isTeamFormat();
    }

    public List<Player> getCompetitors() {
        if (isTeamScorecard()) {
            return participants.stream().toList();
        }
        return player == null ? List.of() : List.of(player);
    }

    public String getDisplayName() {
        if (isTeamScorecard()) {
            return teamName == null || teamName.isBlank() ? "Joukkue" : teamName;
        }
        return player == null ? "" : player.getFirstName() + " " + player.getLastName();
    }

    public String getParticipantNames() {
        return getCompetitors().stream()
                .map(participant -> participant.getFirstName() + " " + participant.getLastName())
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .reduce((first, second) -> first + ", " + second)
                .orElse("");
    }


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
