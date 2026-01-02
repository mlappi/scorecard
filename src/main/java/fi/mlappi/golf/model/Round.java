package fi.mlappi.golf.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

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
public class Round {
	
	@Id
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "sequence", allocationSize = 10)
	private Long id;
    @NonNull
	private String name;	    
	
    @NonNull
//    psql type: @Column(columnDefinition = "timestamptz")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern="dd.MM.yyyy HH:mm")
	private Date date;

	@ManyToOne(fetch=FetchType.EAGER)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Course course;

	@ManyToOne
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Game game;

    @NumberFormat(style = Style.CURRENCY)
	private Double bet;

    @Transient
	private Map<Integer, Set<Player>> winMap = new HashMap<>();    

	public Round() {		
	}

	public long getPar(int hole) {
		for(Hole h : course.getHole()) {
			if(h.getHole() == hole) {
				return h.getPar();
			}		
		}
		return 1;
	}
}
