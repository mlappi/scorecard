package fi.mlappi.golf.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import lombok.Data;
import lombok.NonNull;

/**
 *
 * @author mlappi
 */
@Entity
@Data
public class Course {
	
	@Id
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "sequence", allocationSize = 10)
	private Long id;
    @NonNull
	private String name;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Hole> hole;

	public Course() {
		hole = new ArrayList<Hole>();
	}

}
