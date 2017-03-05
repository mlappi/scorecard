package fi.mlappi.golf.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

/**
 *
 * @author mlappi
 */
@Entity
@Data
public class Game {
	
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    @NonNull
	private String name;
    @NonNull
	private String date;
    @NonNull
	private Double bet;
    @OneToOne(fetch=FetchType.EAGER)
    private Course course;
    
    @Transient
	private Map<Integer, Set<Long>> winMap = new HashMap<>();
    

	public Game() {
		
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
