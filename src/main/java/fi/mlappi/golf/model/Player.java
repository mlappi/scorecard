package fi.mlappi.golf.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author mlappi
 */
@Entity
@Data
@AllArgsConstructor
public class Player {

	@Id
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "sequence", allocationSize = 10)
    private Long id;
    @Column
    @NotEmpty
    private String firstName;
    @Column
    @NotEmpty
    private String lastName;
    @Column
    @NumberFormat(style = Style.CURRENCY)
    private Double hcp;
    @Column
    private String email;

    public Player() {
    	
    }
    
}
