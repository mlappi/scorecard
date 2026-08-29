package fi.mlappi.golf.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import fi.mlappi.golf.model.Player;

@Repository
public interface PlayerRepository extends CrudRepository<Player, Long> {
	List<Player> findByLastNameIgnoreCase(String lastName);
	List<Player> findByFirstNameIgnoreCase(String firstName);
	List<Player> findByLastNameContainingOrFirstNameContainingAllIgnoreCase(String firstName, String lastName);
	List<Player> findByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);
	Optional<Player> findByExternalUserId(Long externalUserId);
	
}
