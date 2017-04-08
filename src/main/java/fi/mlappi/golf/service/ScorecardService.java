package fi.mlappi.golf.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.mlappi.golf.model.Player;
import fi.mlappi.golf.model.Round;
import fi.mlappi.golf.model.Scorecard;
import fi.mlappi.golf.repository.RoundRepository;
import fi.mlappi.golf.repository.ScorecardRepository;



@Service("ScorecardService")
public class ScorecardService {

	@Autowired
	ScorecardRepository scorecardRepository;
	@Autowired
	RoundRepository roundRepository;
	
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
    	return scorecardRepository.findOne(id);
    }   

    public Scorecard save(Scorecard s) {
    	return scorecardRepository.save(s);
    }


    public void delete(long id) {
    	scorecardRepository.delete(id);
    }
    
	public List<Scorecard> countWins(Long roundId) {		
		List<Scorecard> scores = findByRoundId(roundId);
		Round round = roundRepository.findOne(roundId);

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
