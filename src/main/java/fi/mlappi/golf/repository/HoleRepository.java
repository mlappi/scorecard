package fi.mlappi.golf.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import fi.mlappi.golf.model.Hole;

@Repository
public interface HoleRepository extends CrudRepository<Hole, Long> {
	
	
}
