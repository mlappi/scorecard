package fi.mlappi.golf.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

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
