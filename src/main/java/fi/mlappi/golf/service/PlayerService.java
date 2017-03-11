package fi.mlappi.golf.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.mlappi.golf.model.Player;
import fi.mlappi.golf.repository.PlayerRepository;

@Service("PlayerService")
public class PlayerService {
	
	PlayerRepository repository;

	@Autowired
	public PlayerService(PlayerRepository repository) {
		this.repository = repository;
	}
    
	public List<Player> getAllPlayers() { 
    	List<Player> players = new ArrayList<>();
    	Iterable<Player> iter = repository.findAll();
    	for (Player player : iter) {
    		players.add(player);
		}
        return players;
    }

//    public List<Player> searchPlayersByName(String name) {
//        Comparator<Player> groupByComparator = Comparator.comparing(Player::getName)
//                                                    .thenComparing(Player::getLastName);
//        List<Player> result = playerList
//                .stream()
//                .filter(e -> e.getName().equalsIgnoreCase(name) || e.getLastName().equalsIgnoreCase(name))
//                .sorted(groupByComparator)
//                .collect(Collectors.toList());
//        return result;
//    }

//    public Player getPlayer(long id) throws Exception {
//        Optional<Player> match
//                = playerList.stream()
//                .filter(e -> e.getId() == id)
//                .findFirst();
//        if (match.isPresent()) {
//            return match.get();
//        } else {
//            throw new Exception("The Player id " + id + " not found");
//        }
//    }   

    public Player save(Player player) {
    	return repository.save(player);
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
