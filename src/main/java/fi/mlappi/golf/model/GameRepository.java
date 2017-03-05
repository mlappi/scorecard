package fi.mlappi.golf.model;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends CrudRepository<Game, Long> {
	List<Game> findByNameContainingIgnoreCase(String name);
	
}
