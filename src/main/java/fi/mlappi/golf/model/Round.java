package fi.mlappi.golf.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

import lombok.Data;
import lombok.NonNull;

/**
 *
 * @author mlappi
 */
@Entity
@Data
public class Round {
	
	@Id
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "sequence", allocationSize = 10)
	private Long id;
    @NonNull
	private String name;
	private String courseName;    
    @NonNull
    @Column(columnDefinition = "timestamptz")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern="dd.MM.yyyy HH:mm")
	private Date date;
    @OneToOne(fetch=FetchType.EAGER)
    private Course course;
    @ManyToOne
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
