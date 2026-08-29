package fi.mlappi.golf.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
		if (s.getRound() == null || (!s.isTeamScorecard() && s.getPlayer() == null)) {
			return scorecardRepository.save(s);
		}

		String competitorKey = s.isTeamScorecard()
				? s.getPlayFormat() + "-" + s.getExternalCompetitorId()
				: "PLAYER-" + s.getPlayer().getId();
		String key = s.getRound().getId() + "-" + competitorKey;
		Object lock = saveLocks.computeIfAbsent(key, k -> new Object());

		synchronized (lock) {
			try {
			Optional<Scorecard> existing = findExisting(s);
			if (existing.isPresent()) {
				Scorecard e = existing.get();
				copyEditableFields(s, e);
				return scorecardRepository.save(e);
			}
			return scorecardRepository.save(s);
		} catch (DataIntegrityViolationException ex) {
			Optional<Scorecard> existingAfter = findExisting(s);
			if (existingAfter.isPresent()) {
				Scorecard e = existingAfter.get();
				copyEditableFields(s, e);
				return scorecardRepository.save(e);
			}
			throw ex;
		} finally {
			// cleanup lock to avoid leak
			saveLocks.remove(key);
		}
	}
	}

	private Optional<Scorecard> findExisting(Scorecard scorecard) {
		if (scorecard.isTeamScorecard()) {
			if (scorecard.getExternalCompetitorId() == null) {
				return Optional.empty();
			}
			return scorecardRepository.findByRoundIdAndPlayFormatAndExternalCompetitorId(
					scorecard.getRound().getId(), scorecard.getPlayFormat(), scorecard.getExternalCompetitorId());
		}
		return scorecardRepository.findByRoundIdAndPlayerId(
				scorecard.getRound().getId(), scorecard.getPlayer().getId());
	}

	private void copyEditableFields(Scorecard source, Scorecard target) {
		target.setHole1(source.getHole1()); target.setHole2(source.getHole2());
		target.setHole3(source.getHole3()); target.setHole4(source.getHole4());
		target.setHole5(source.getHole5()); target.setHole6(source.getHole6());
		target.setHole7(source.getHole7()); target.setHole8(source.getHole8());
		target.setHole9(source.getHole9()); target.setHole10(source.getHole10());
		target.setHole11(source.getHole11()); target.setHole12(source.getHole12());
		target.setHole13(source.getHole13()); target.setHole14(source.getHole14());
		target.setHole15(source.getHole15()); target.setHole16(source.getHole16());
		target.setHole17(source.getHole17()); target.setHole18(source.getHole18());
		target.setPlayer(source.getPlayer());
		target.setPlayFormat(source.getPlayFormat());
		target.setTeamName(source.getTeamName());
		target.setExternalCompetitorId(source.getExternalCompetitorId());
		target.setPlayingHcp(source.getPlayingHcp());
		target.getParticipants().clear();
		target.getParticipants().addAll(source.getParticipants());
		target.setWin(source.getWin());
	}


    public void delete(long id) {
		scorecardRepository.deleteById(id);
    }
    
	public List<Scorecard> countWins(Long roundId) {		
		if (roundId == null || roundId == 0) {
			return new ArrayList<>();
		}
		List<Scorecard> scores = findByRoundId(roundId);
		Round round = roundRepository.findById(roundId).orElse(null);
		if (round == null || scores.isEmpty()) {
			return scores;
		}

		RoundWinResult roundWinResult = calculateRoundWins(round, scores);
		round.getWinMap().clear();
		List<Integer> holes = new ArrayList<>(roundWinResult.winnersByHole.keySet());
		holes.sort(Integer::compareTo);

		Map<Scorecard, Long> winCentsByScorecard = new IdentityHashMap<>();
		for (Integer hole : holes) {
			Set<Scorecard> winners = roundWinResult.winnersByHole.get(hole);
			if (winners == null || winners.isEmpty()) {
				continue;
			}
			long holeCents = roundWinResult.winCentsByHole.getOrDefault(hole, 0L);
			List<Scorecard> sortedWinners = new ArrayList<>(winners);
			sortedWinners.sort(Comparator.comparing(Scorecard::getId, Comparator.nullsLast(Long::compareTo)));
			long winnerBaseCents = holeCents / sortedWinners.size();
			long winnerRemainder = holeCents % sortedWinners.size();
			for (int i = 0; i < sortedWinners.size(); i++) {
				long add = winnerBaseCents + (i < winnerRemainder ? 1 : 0);
				winCentsByScorecard.merge(sortedWinners.get(i), add, Long::sum);
			}
		}

		for (Scorecard scorecard : scores) {
			scorecard.setWin(0d);
			scorecard.setSkinsWon(0);
			scorecard.getWinners().clear();
			for (Integer hole : holes) {
				Set<Scorecard> winners = roundWinResult.winnersByHole.get(hole);
				if (winners != null && winners.contains(scorecard)) {
					scorecard.getWinners().add(hole);
				}
			}
			scorecard.setSkinsWon(scorecard.getWinners().size());
			Long winCents = winCentsByScorecard.get(scorecard);
			if (winCents != null && winCents > 0) {
				scorecard.setWin(BigDecimal.valueOf(winCents).movePointLeft(2).doubleValue());
			}
			scorecardRepository.save(scorecard);
		}
		return scores;
	}

	public double getPlayerStake(Round round, List<Scorecard> scores) {
		if (round == null || scores == null || scores.isEmpty()) {
			return 0d;
		}
		RoundWinResult roundWinResult = calculateRoundWins(round, scores);
		long playerStakeCents = roundWinResult.awardedPotCents / scores.size();
		return BigDecimal.valueOf(playerStakeCents).movePointLeft(2).doubleValue();
	}

	private RoundWinResult calculateRoundWins(Round round, List<Scorecard> scores) {
		RoundWinResult result = new RoundWinResult();
		long carryCents = 0;
		for (int hole = 1; hole < 19; hole++) {
			carryCents += getHolePotCents(round, scores.size(), hole);
			Set<Scorecard> winners = getWinnersForLowestScore(scores, hole, round);
			if (!winners.isEmpty()) {
				result.winnersByHole.put(hole, winners);
				result.winCentsByHole.put(hole, carryCents);
				result.awardedPotCents += carryCents;
				carryCents = 0;
			}
		}
		return result;
	}

	private long getHolePotCents(Round round, int playerCount, int hole) {
		BigDecimal bet = BigDecimal.valueOf(round.getBet() == null ? 0d : round.getBet());
		long betCents = bet.setScale(2, RoundingMode.HALF_UP).movePointRight(2).longValueExact();
		if (round.isBasicSkin()) {
			return betCents * playerCount;
		}
		long roundPotCents = betCents * playerCount;
		long holeBaseCents = roundPotCents / 18;
		long holeRemainder = roundPotCents % 18;
		return holeBaseCents + (hole <= holeRemainder ? 1 : 0);
	}

	private Set<Scorecard> getWinnersForLowestScore(List<Scorecard> scores, int hole, Round round) {
		Set<Scorecard> lowest = Collections.newSetFromMap(new IdentityHashMap<>());
		Integer bestScore = null;
		for (Scorecard scorecard : scores) {
			Integer score = scorecard.getScore(hole);
			if (score == null) {
				continue;
			}
			if (bestScore == null || score < bestScore) {
				bestScore = score;
				lowest.clear();
				lowest.add(scorecard);
			} else if (score.equals(bestScore)) {
				lowest.add(scorecard);
			}
		}
		if (lowest.size() > 1 && (round.isBasicSkin() || bestScore == null || bestScore >= round.getPar(hole))) {
			lowest.clear();
		}
		return lowest;
	}

	private static class RoundWinResult {
		private final Map<Integer, Set<Scorecard>> winnersByHole = new HashMap<>();
		private final Map<Integer, Long> winCentsByHole = new HashMap<>();
		private long awardedPotCents;
	}
    
}
