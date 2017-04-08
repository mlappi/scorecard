package fi.mlappi.golf.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author mlappi
 */
@Entity
@Data
public class Hole {
	
	@Id
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "sequence", allocationSize = 20)
	private Long id;
	private int hole;
	private Integer par;
	private int hcp;
    @ManyToOne    
	private Course course;

	public Hole() {
		
	}

}
