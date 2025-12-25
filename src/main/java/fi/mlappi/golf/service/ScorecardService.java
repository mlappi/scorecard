package fi.mlappi.golf.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.mlappi.golf.model.Player;
import fi.mlappi.golf.model.Round;
import fi.mlappi.golf.model.Scorecard;
import fi.mlappi.golf.repository.RoundRepository;
import fi.mlappi.golf.repository.ScorecardRepository;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.dao.DataIntegrityViolationException;

@Service("ScorecardService")
public class ScorecardService {

	@Autowired
	ScorecardRepository scorecardRepository;
	@Autowired
	RoundRepository roundRepository;

	// locks for serializing saves per round+player to avoid duplicate inserts under concurrency
	private final ConcurrentMap<String, Object> saveLocks = new ConcurrentHashMap<>();
	
    public List<Scorecard> getAllScorecards() {
		List<Scorecard> scores = new ArrayList<>();
		Iterable<Scorecard> iter = scorecardRepository.findAll();
		for (Scorecard s : iter) {
			scores.add(s);
		}

		return scores;

    }
    
	public List<Scorecard> findByRoundId(Long roundId)  {
    	return scorecardRepository.findByRoundId(roundId);
    }   

    public Scorecard find(long id)  {
		return scorecardRepository.findById(id).orElse(null);
    }   

    public Scorecard save(Scorecard s) {
    	// Prevent duplicate scorecards for the same round+player by updating existing one
    	if (s.getRound() == null || s.getPlayer() == null) {
    		return scorecardRepository.save(s);
    	}

    	String key = s.getRound().getId() + "-" + s.getPlayer().getId();
    	Object lock = saveLocks.computeIfAbsent(key, k -> new Object());

    	synchronized (lock) {
    		try {
    			// check existing first
    			Optional<Scorecard> existing = scorecardRepository.findByRoundIdAndPlayerId(s.getRound().getId(), s.getPlayer().getId());
    			if (existing.isPresent()) {
    				Scorecard e = existing.get();
    				// copy editable fields from incoming `s` into the persisted entity `e`
    				e.setHole1(s.getHole1());
    				e.setHole2(s.getHole2());
    				e.setHole3(s.getHole3());
    				e.setHole4(s.getHole4());
    				e.setHole5(s.getHole5());
    				e.setHole6(s.getHole6());
    				e.setHole7(s.getHole7());
    				e.setHole8(s.getHole8());
    				e.setHole9(s.getHole9());
    				e.setHole10(s.getHole10());
    				e.setHole11(s.getHole11());
    				e.setHole12(s.getHole12());
    				e.setHole13(s.getHole13());
    				e.setHole14(s.getHole14());
    				e.setHole15(s.getHole15());
    				e.setHole16(s.getHole16());
    				e.setHole17(s.getHole17());
    				e.setHole18(s.getHole18());
    				e.setWin(s.getWin());
    				return scorecardRepository.save(e);
    			}
    			return scorecardRepository.save(s);
    		} catch (DataIntegrityViolationException ex) {
    			// Race still possible; try to update existing
    			Optional<Scorecard> existingAfter = scorecardRepository.findByRoundIdAndPlayerId(s.getRound().getId(), s.getPlayer().getId());
    			if (existingAfter.isPresent()) {
    				Scorecard e = existingAfter.get();
    				e.setHole1(s.getHole1());
    				e.setHole2(s.getHole2());
    				e.setHole3(s.getHole3());
    				e.setHole4(s.getHole4());
    				e.setHole5(s.getHole5());
    				e.setHole6(s.getHole6());
    				e.setHole7(s.getHole7());
    				e.setHole8(s.getHole8());
    				e.setHole9(s.getHole9());
    				e.setHole10(s.getHole10());
    				e.setHole11(s.getHole11());
    				e.setHole12(s.getHole12());
    				e.setHole13(s.getHole13());
    				e.setHole14(s.getHole14());
    				e.setHole15(s.getHole15());
    				e.setHole16(s.getHole16());
    				e.setHole17(s.getHole17());
    				e.setHole18(s.getHole18());
    				e.setWin(s.getWin());
    				return scorecardRepository.save(e);
    			}
    			throw ex;
    		} finally {
    			// cleanup lock to avoid leak
    			saveLocks.remove(key);
    		}
    	}
    }


    public void delete(long id) {
		scorecardRepository.deleteById(id);
    }
    
	public List<Scorecard> countWins(Long roundId) {		
		List<Scorecard> scores = findByRoundId(roundId);
		Round round = roundRepository.findById(roundId).orElse(null);

		double pot = round.getBet() * scores.size();
		for (int i = 1; i < 19; i++) {
			Set<Player> players = getPlayersForLowestScore(scores, i, round);
			if (!players.isEmpty()) {
				round.getWinMap().put(i, players);
			}
		}

		double holeValue = pot / round.getWinMap().size();

		for (Scorecard scorecard : scores) {
				double wins = 0;
				Set<Integer> holeset = round.getWinMap().keySet();
				for (Integer hole : holeset) {
					if (round.getWinMap().get(hole).contains(scorecard.getPlayer())) {
						wins = wins + (holeValue / round.getWinMap().get(hole).size());
						scorecard.getWinners().add(hole);
					}
				}
				if (wins > 0) {
					scorecard.setWin(BigDecimal.valueOf(wins).setScale(2, RoundingMode.FLOOR).doubleValue());
					scorecardRepository.save(scorecard);
				}
		}
		return scores;
	}

	private Set<Player> getPlayersForLowestScore(List<Scorecard> scores, int hole, Round round) {
		Set<Player> players = new HashSet<>();
		Player bestPlayer = null;
		long bestScore = 0;
		for (Scorecard scorecard : scores) {
			if (bestScore == 0) {
				bestPlayer = scorecard.getPlayer();
				bestScore = scorecard.getScore(hole);
				players.add(bestPlayer);
			} else {
				if (bestScore > scorecard.getScore(hole)) {
					players.remove(bestPlayer);
					bestPlayer = scorecard.getPlayer();
					bestScore = scorecard.getScore(hole);
					players.add(bestPlayer);
				} else if (bestScore == scorecard.getScore(hole) && round.getPar(hole) <= bestScore) {					
					players.remove(bestPlayer);
					bestPlayer = null;
				}
				/*
				 * Jenkkiskinisääntö, birkulla aina rahaa jos ei ilkkoja
				 */
				else if (bestScore == scorecard.getScore(hole) && round.getPar(hole) > bestScore) {
					bestPlayer = null;
					players.add(scorecard.getPlayer());
				}
			}
		}
		return players;
	}
    
}
