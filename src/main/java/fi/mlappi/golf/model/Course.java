package fi.mlappi.golf.model;

import java.util.ArrayList;
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

import lombok.Data;
import lombok.NonNull;
import lombok.ToString;
import lombok.EqualsAndHashCode;

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

	@OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@OrderBy(value="hole")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<Hole> hole;

	public Course() {
		hole = new ArrayList<Hole>();
	}

	public int getCountTotal() {
		return getCountOut() + getCountIn();
	}
	
	public int getCountOut() {
		int sum = 0;
		for(Hole h : hole) {
			if(h.getPar() == null ) {
				return sum;
			}
			sum = sum + h.getPar();
			if(h.getHole() == 9) {
				break;
			}
		}
		return sum;
	}

	public int getCountIn() {
		int sum = 0;
		for(Hole h : hole) {
			if(h.getPar() == null ) {
				return sum;
			}
			if(h.getHole() < 10) {
				continue;
			}
			sum = sum + h.getPar();
		}		
		return sum;
	}

}
