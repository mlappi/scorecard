package fi.mlappi.golf.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.NonNull;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author mlappi
 */
@Entity
@Data
@Slf4j
public class Game {

	@Id
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "sequence", allocationSize = 10)
	private Long id;
	
	@NonNull
	private String name;
	
	@NonNull
	//psql type: @Column(columnDefinition = "timestamptz")
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd.MM.yyyy HH:mm")
	private Date date;

	@OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@OrderBy(value="date")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<Round> round;

	public Game() {
		round = new ArrayList<>();
	}

	public Round getRound(long id) {
		if (round != null) {
			for (Round r : round) {
				if (r.getId() == id) {
					return r;
				}
			}
		}
		return null;
	}
}
