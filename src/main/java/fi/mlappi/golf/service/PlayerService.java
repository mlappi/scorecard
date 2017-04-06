package fi.mlappi.golf.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.mlappi.golf.model.Player;
import fi.mlappi.golf.model.Scorecard;
import fi.mlappi.golf.repository.PlayerRepository;
import fi.mlappi.golf.repository.ScorecardRepository;

@Service("PlayerService")
public class PlayerService {
	
	@Autowired
	PlayerRepository repository;
	@Autowired
	ScorecardRepository scorecardRepository;

	    
	public List<Player> getAllPlayers() { 
    	List<Player> players = new ArrayList<>();
    	Iterable<Player> iter = repository.findAll();
    	for (Player player : iter) {
    		players.add(player);
		}
        return players;
    }

    public Player save(Player player) {
    	return repository.save(player);
    }

	public List<Scorecard> findScorecards(Long id) {
		return scorecardRepository.findByPlayerId(id);
	}

	public Player find(Long id) {
		return repository.findOne(id);
	}

	public void delete(Long id) {
		repository.delete(id);
	}

	public List<Player> search(String playerName) {
		return repository.findByLastNameContainingOrFirstNameContainingAllIgnoreCase(playerName, playerName);		
	}

}
