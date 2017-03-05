package fi.mlappi.golf.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;

/**
 *
 * @author mlappi
 */
@Entity
@Data
public class Hole {
	
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
	private int hole;
	private int par;
	private int hcp;
    @ManyToOne    
	private Course course;

	public Hole() {
		
	}

}
