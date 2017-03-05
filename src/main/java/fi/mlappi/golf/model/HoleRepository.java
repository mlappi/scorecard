package fi.mlappi.golf.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HoleRepository extends CrudRepository<Hole, Long> {
	
	
}
