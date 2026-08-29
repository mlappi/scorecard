package fi.mlappi.golf.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import fi.mlappi.golf.model.Course;
import fi.mlappi.golf.model.Game;
import fi.mlappi.golf.model.Round;
import fi.mlappi.golf.model.Scorecard;
import fi.mlappi.golf.service.CourseService;
import fi.mlappi.golf.service.GameService;
import fi.mlappi.golf.service.ScorecardService;

@ExtendWith(MockitoExtension.class)
class GameControllerTest {

    @Mock
    private GameService gameService;

    @Mock
    private ScorecardService scoreService;

    @Mock
    private CourseService courseService;

    @InjectMocks
    private GameController controller;

    @Test
    void gamesReturnsListViewWithGames() {
        List<Game> games = List.of(new Game());
        when(gameService.getAllGames()).thenReturn(games);
        ModelMap model = new ModelMap();

        String view = controller.games(model);

        assertThat(view).isEqualTo("list-games");
        assertThat(model.get("games")).isSameAs(games);
    }

    @Test
    void addCreatesGameWithDateAndCourseList() {
        Course course = new Course();
        course.setId(3L);
        course.setName("Kentta");
        when(courseService.getAllCourses()).thenReturn(List.of(course));
        ModelMap model = new ModelMap();

        String view = controller.add(model);

        assertThat(view).isEqualTo("new-game");
        Game game = (Game) model.get("game");
        assertThat(game.getDate()).isNotNull();
        Map<Long, String> courseList = (Map<Long, String>) model.get("courseList");
        assertThat(courseList).containsEntry(3L, "Kentta");
    }

    @Test
    void addRoundAddsRoundToGame() {
        Game game = new Game();
        game.setId(1L);
        when(gameService.find(1L)).thenReturn(game);
        when(courseService.getAllCourses()).thenReturn(Collections.emptyList());
        ModelMap model = new ModelMap();

        String view = controller.addRound(model, 1L);

        assertThat(view).isEqualTo("new-game");
        assertThat(game.getRound()).hasSize(1);
    }

    @Test
    void editLoadsGameAndReturnsFormView() {
        Game game = new Game();
        when(gameService.find(9L)).thenReturn(game);
        when(courseService.getAllCourses()).thenReturn(Collections.emptyList());
        ModelMap model = new ModelMap();

        ModelAndView view = controller.edit(model, 9L);

        assertThat(view.getViewName()).isEqualTo("new-game");
        assertThat(model.get("game")).isSameAs(game);
    }

    @Test
    void removeDeletesGameWhenNoScorecards() {
        Game game = new Game();
        Round round = new Round();
        round.setId(7L);
        game.getRound().add(round);
        when(gameService.find(2L)).thenReturn(game);
        when(scoreService.findByRoundId(7L)).thenReturn(Collections.emptyList());
        when(gameService.getAllGames()).thenReturn(Collections.singletonList(game));
        ModelMap model = new ModelMap();

        String view = controller.remove(model, 2L);

        assertThat(view).isEqualTo("list-games");
        verify(gameService).delete(2L);
    }

    @Test
    void removeKeepsGameWhenScorecardsExist() {
        Game game = new Game();
        Round round = new Round();
        round.setId(7L);
        game.getRound().add(round);
        when(gameService.find(2L)).thenReturn(game);
        when(scoreService.findByRoundId(7L)).thenReturn(List.of(new Scorecard()));
        when(gameService.getAllGames()).thenReturn(Collections.singletonList(game));
        ModelMap model = new ModelMap();

        String view = controller.remove(model, 2L);

        assertThat(view).isEqualTo("list-games");
        assertThat(model.get("errormessage")).isEqualTo(
                "Poistaaksesi pelin, poista ensin kaikki siihen liittyvät tuloskortit.");
        verify(gameService, never()).delete(any(Long.class));
    }

    @Test
    void removeRoundDeletesRoundWhenNoScores() {
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.removeRound(new ModelMap(), 1L, 4L, redirectAttributes);

        assertThat(view).isEqualTo("redirect:/game/edit/1");
        assertThat(redirectAttributes.getFlashAttributes()).containsKey("message");
        verify(gameService).removeRound(4L);
    }

    @Test
    void removeRoundDeletesRoundWhenScoresExist() {
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.removeRound(new ModelMap(), 1L, 4L, redirectAttributes);

        assertThat(view).isEqualTo("redirect:/game/edit/1");
        assertThat(redirectAttributes.getFlashAttributes()).containsKey("message");
        verify(gameService).removeRound(4L);
    }

    @Test
    void savePersistsGameAndRoundsWhenValid() {
        Game game = new Game();
        Round round = new Round();
        Course course = new Course();
        course.setId(5L);
        round.setCourse(course);
        game.getRound().add(round);
        ModelMap model = new ModelMap();
        BindingResult result = new BeanPropertyBindingResult(game, "game");

        doAnswer(invocation -> {
            Game saved = invocation.getArgument(0);
            saved.setId(11L);
            return 11L;
        }).when(gameService).save((Game) any());
        when(gameService.save(any(Round.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(courseService.find(5L)).thenReturn(course);

        String view = controller.save(model, game, result);

        assertThat(view).isEqualTo("new-game");
        assertThat(model.get("message")).isEqualTo("Uusi peli on lisätty onnistuneesti.");
        assertThat(model.get("idGame")).isEqualTo(11L);
        verify(gameService).save(round);
        verify(gameService).save(game);
    }

    @Test
    void saveWithErrorsReturnsFormWithoutPersisting() {
        Game game = new Game();
        ModelMap model = new ModelMap();
        BindingResult result = new BeanPropertyBindingResult(game, "game");
        result.reject("invalid");
        when(courseService.getAllCourses()).thenReturn(Collections.emptyList());

        String view = controller.save(model, game, result);

        assertThat(view).isEqualTo("new-game");
        verify(gameService, never()).save(any(Game.class));
    }

    @Test
    void searchReturnsMatchingGames() {
        List<Game> games = List.of(new Game());
        when(gameService.search("Testi")).thenReturn(games);
        ModelMap model = new ModelMap();

        String view = controller.search(model, "Testi");

        assertThat(view).isEqualTo("list-games");
        assertThat(model.get("games")).isSameAs(games);
    }
}
