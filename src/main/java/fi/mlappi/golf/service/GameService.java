package fi.mlappi.golf.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.mlappi.golf.model.Course;
import fi.mlappi.golf.model.Game;
import fi.mlappi.golf.model.Hole;
import fi.mlappi.golf.repository.CourseRepository;
import fi.mlappi.golf.repository.GameRepository;
import fi.mlappi.golf.repository.HoleRepository;

@Service("GameService")
public class GameService {

	private static final int[] holes = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18 };
	private static final int[] pars = { 5, 4, 3, 4, 3, 5, 4, 3, 4, 4, 5, 4, 3, 4, 3, 5, 4, 4 };

	GameRepository gameRepository;
	CourseRepository courseRepository;
	HoleRepository holeRepository;

	@Autowired
	public GameService(GameRepository gameRepository, CourseRepository courseRepository, HoleRepository holeRepository) {
		this.gameRepository = gameRepository;
		this.courseRepository = courseRepository;
		this.holeRepository = holeRepository;		
	}

	public List<Game> getAllGames() {
		List<Game> games = new ArrayList<>();
		Iterable<Game> iter = gameRepository.findAll();
		for (Game g : iter) {
			games.add(g);
		}

		return games;
	}

	public List<Game> search(String name) {
//		Comparator<Game> groupByComparator = Comparator.comparing(Game::getName);
//		List<Game> result = gameList.stream().filter(e -> e.getName().equalsIgnoreCase(name)).sorted(groupByComparator)
//				.collect(Collectors.toList());
		return gameRepository.findByNameContainingIgnoreCase(name);
	}

	public Game find(long id) {
		return gameRepository.findOne(id);
	}

	public long save(Game game) {
		if(game.getCourse() == null) {
			game.setCourse(getMuurame());
		}
		return gameRepository.save(game).getId();
	}

	private Course getMuurame() {
		if(courseRepository.count() == 0) {
			Course c = new Course();
			c.setName("Muurame Golf");
			c = courseRepository.save(c);
			for (int i : holes) {
				Hole h = new Hole();
				h.setCourse(c);
				h.setHole(i);
				h.setPar(pars[(i-1)]);
				h = holeRepository.save(h);
				c.getHole().add(h);
			}
			return c;
		}
		else {
			return courseRepository.findAll().iterator().next();
		}
	}

	public void delete(long id) {
		gameRepository.delete(id);
	}
}
