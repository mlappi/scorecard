package fi.mlappi.golf.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import fi.mlappi.golf.model.Game;

@Repository
public interface GameRepository extends CrudRepository<Game, Long> {
	List<Game> findByNameContainingIgnoreCase(String name);
	
}
