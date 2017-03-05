package fi.mlappi.golf.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.validator.constraints.NotEmpty;

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
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    @Column
    @NotEmpty
    private String firstName;
    @Column
    @NotEmpty
    private String lastName;
    @Column
    private String hcp;
    @Column
    private String email;

    public Player() {
    	
    }
    
}
