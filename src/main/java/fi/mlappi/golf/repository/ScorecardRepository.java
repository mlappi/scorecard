package fi.mlappi.golf.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import fi.mlappi.golf.model.Course;
import fi.mlappi.golf.model.Scorecard;

@Repository
public interface ScorecardRepository extends CrudRepository<Scorecard, Long> {
	
	public List<Scorecard> findByRoundId(Long roundId);
	
	
}
