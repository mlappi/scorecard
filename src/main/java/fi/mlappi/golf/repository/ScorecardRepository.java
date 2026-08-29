package fi.mlappi.golf.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import fi.mlappi.golf.model.Scorecard;
import java.util.Optional;

@Repository
public interface ScorecardRepository extends CrudRepository<Scorecard, Long> {
	
	public List<Scorecard> findByRoundId(Long roundId);
	public List<Scorecard> findByPlayerId(Long playerId);

	public Optional<Scorecard> findByRoundIdAndPlayerId(Long roundId, Long playerId);
	public Optional<Scorecard> findByRoundIdAndPlayFormatAndExternalCompetitorId(
			Long roundId, fi.mlappi.golf.model.PlayFormat playFormat, String externalCompetitorId);
	
	
}
