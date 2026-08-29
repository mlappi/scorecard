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

    public Player findOrCreateByExternalUserId(Long externalUserId, String firstName, String lastName, Double hcp) {
		if (externalUserId == null) {
			return findOrCreateByName(firstName, lastName, hcp);
		}
		Player player = repository.findByExternalUserId(externalUserId).orElse(null);
		if (player == null) {
			player = findFirstByName(firstName, lastName);
		}
		if (player == null) {
			player = new Player();
			player.setFirstName(firstName);
			player.setLastName(lastName);
			player.setEmail(null);
		}
		player.setExternalUserId(externalUserId);
		player.setFirstName(firstName);
		player.setLastName(lastName);
		if (hcp != null) {
			player.setHcp(hcp);
		} else if (player.getHcp() == null) {
			player.setHcp(0d);
		}
		return repository.save(player);
    }

	public List<Scorecard> findScorecards(Long id) {
		return scorecardRepository.findByPlayerId(id);
	}

	public Player find(Long id) {
		return repository.findById(id).orElse(null);
	}

	public void delete(Long id) {
		repository.deleteById(id);
	}

	public List<Player> search(String playerName) {
		return repository.findByLastNameContainingOrFirstNameContainingAllIgnoreCase(playerName, playerName);		
	}

	public boolean existsByName(String firstName, String lastName) {
		if (firstName == null || lastName == null) {
			return false;
		}
		return !repository.findByFirstNameIgnoreCaseAndLastNameIgnoreCase(firstName, lastName).isEmpty();
	}

	private Player findOrCreateByName(String firstName, String lastName, Double hcp) {
		Player player = findFirstByName(firstName, lastName);
		if (player == null) {
			player = new Player();
			player.setFirstName(firstName);
			player.setLastName(lastName);
			player.setEmail(null);
		}
		player.setHcp(hcp != null ? hcp : 0d);
		return repository.save(player);
	}

	private Player findFirstByName(String firstName, String lastName) {
		if (firstName == null || lastName == null) {
			return null;
		}
		List<Player> players = repository.findByFirstNameIgnoreCaseAndLastNameIgnoreCase(firstName, lastName);
		return players.stream()
				.min((p1, p2) -> Long.compare(p1.getId() != null ? p1.getId() : Long.MAX_VALUE,
						p2.getId() != null ? p2.getId() : Long.MAX_VALUE))
				.orElse(null);
	}

}
