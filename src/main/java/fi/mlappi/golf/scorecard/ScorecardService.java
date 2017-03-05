package fi.mlappi.golf.scorecard;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import fi.mlappi.golf.model.Scorecard;



public class ScorecardService {

    List<Scorecard> scorecardList = ScorecardList.getInstance();

    public List<Scorecard> getAllScorecards() {       
        return scorecardList;
    }
    
    public Scorecard getScorecard(long id) throws Exception {
        Optional<Scorecard> match
                = scorecardList.stream()
                .filter(e -> e.getId() == id)
                .findFirst();
        if (match.isPresent()) {
            return match.get();
        } else {
            throw new Exception("The Scorecard id " + id + " not found");
        }
    }   

    public long addScorecard(Scorecard game) {
    	scorecardList.add(game);
        return game.getId();
    }

    public boolean updateScorecard(Scorecard game) {
        int matchIdx = 0;
        Optional<Scorecard> match = scorecardList.stream()
                .filter(c -> c.getId() == game.getId())
                .findFirst();
        if (match.isPresent()) {
            matchIdx = scorecardList.indexOf(match.get());
            scorecardList.set(matchIdx, game);
            return true;
        } else {
            return false;
        }
    }

    public boolean delete(long id) {
        Predicate<Scorecard> game = e -> e.getId() == id;
        if (scorecardList.removeIf(game)) {
            return true;
        } else {
            return false;
        }
    }
}
