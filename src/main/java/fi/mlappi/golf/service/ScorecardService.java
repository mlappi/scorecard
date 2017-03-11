package fi.mlappi.golf.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.mlappi.golf.model.Scorecard;
import fi.mlappi.golf.repository.ScorecardRepository;



@Service("ScorecardService")
public class ScorecardService {

	@Autowired
	ScorecardRepository scorecardRepository;

    public List<Scorecard> getAllScorecards() {
		List<Scorecard> scores = new ArrayList<>();
		Iterable<Scorecard> iter = scorecardRepository.findAll();
		for (Scorecard s : iter) {
			scores.add(s);
		}

		return scores;

    }
    
    public List<Scorecard> findByGameId(Long gameId)  {
    	return scorecardRepository.findByGameId(gameId);
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
}
