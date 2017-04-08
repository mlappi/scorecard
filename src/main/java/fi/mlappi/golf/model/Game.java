package fi.mlappi.golf.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

import fi.mlappi.golf.controller.ScorecardController;
import lombok.Data;
import lombok.NonNull;
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
	@Column(columnDefinition = "timestamptz")
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd.MM.yyyy HH:mm")
	private Date date;
	@NonNull
	@NumberFormat(style = Style.CURRENCY)
	private Double bet;
	@OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
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
