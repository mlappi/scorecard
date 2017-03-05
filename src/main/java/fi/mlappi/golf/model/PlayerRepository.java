package fi.mlappi.golf.model;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends CrudRepository<Player, Long> {
	List<Player> findByLastNameIgnoreCase(String lastName);
	List<Player> findByFirstNameIgnoreCase(String firstName);
	List<Player> findByLastNameContainingOrFirstNameContainingAllIgnoreCase(String firstName, String lastName);
	
}
