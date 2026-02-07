package fi.mlappi.golf.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import fi.mlappi.golf.model.Player;
import fi.mlappi.golf.model.Scorecard;
import fi.mlappi.golf.service.PlayerService;

@ExtendWith(MockitoExtension.class)
class PlayerControllerTest {

    @Mock
    private PlayerService playerService;

    @InjectMocks
    private PlayerController controller;

    @Test
    void homeReturnsIndex() {
        String view = controller.home(new ModelMap());

        assertThat(view).isEqualTo("index");
    }

    @Test
    void playersReturnsListViewWithPlayers() {
        List<Player> players = List.of(new Player());
        when(playerService.getAllPlayers()).thenReturn(players);
        ModelMap model = new ModelMap();

        String view = controller.players(model);

        assertThat(view).isEqualTo("list-players");
        assertThat(model.get("players")).isSameAs(players);
    }

    @Test
    void removeDeletesPlayerWithoutScores() {
        when(playerService.findScorecards(4L)).thenReturn(Collections.emptyList());
        ModelMap model = new ModelMap();

        String view = controller.remove(model, 4L);

        assertThat(view).isEqualTo("list-players");
        assertThat(model.get("message")).isEqualTo("Pelaaja on poistettu onnistuneesti.");
        verify(playerService).delete(4L);
    }

    @Test
    void removeKeepsPlayerWithScores() {
        when(playerService.findScorecards(4L)).thenReturn(List.of(new Scorecard()));
        ModelMap model = new ModelMap();

        String view = controller.remove(model, 4L);

        assertThat(view).isEqualTo("list-players");
        assertThat(model.get("errormessage")).isEqualTo(
                "Pelaajalla on pelattuja kierroksia. Poista ensin kaikki tuloskortit.");
        verify(playerService, never()).delete(any(Long.class));
    }

    @Test
    void saveWithErrorsReturnsForm() {
        Player player = new Player();
        ModelMap model = new ModelMap();
        BindingResult result = new BeanPropertyBindingResult(player, "player");
        result.reject("invalid");

        String view = controller.save(model, player, result);

        assertThat(view).isEqualTo("new-player");
        verify(playerService, never()).save(any(Player.class));
    }

    @Test
    void savePersistsNewPlayerAndRedirectsToList() {
        Player player = new Player();
        ModelMap model = new ModelMap();
        BindingResult result = new BeanPropertyBindingResult(player, "player");

        doAnswer(invocation -> {
            Player saved = invocation.getArgument(0);
            saved.setId(9L);
            return saved;
        }).when(playerService).save(player);
        when(playerService.getAllPlayers()).thenReturn(Collections.singletonList(player));

        String view = controller.save(model, player, result);

        assertThat(view).isEqualTo("list-players");
        assertThat(model.get("message")).isEqualTo("Uusi pelaaja on lisätty onnistuneesti.");
        assertThat(model.get("idPlayer")).isEqualTo(9L);
        verify(playerService).save(player);
    }

    @Test
    void searchReturnsMatchingPlayers() {
        List<Player> players = List.of(new Player());
        when(playerService.search("Teppo")).thenReturn(players);
        ModelMap model = new ModelMap();

        String view = controller.search(model, "Teppo");

        assertThat(view).isEqualTo("list-players");
        assertThat(model.get("players")).isSameAs(players);
    }

    @Test
    void importPlayersRejectsEmptyList() {
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.importPlayers("   ", redirectAttributes);

        assertThat(view).isEqualTo("redirect:/player");
        assertThat(redirectAttributes.getFlashAttributes()).containsKey("errormessage");
        verify(playerService, never()).save(any(Player.class));
    }

    @Test
    void importPlayersRejectsInvalidLines() {
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.importPlayers("SingleName\n", redirectAttributes);

        assertThat(view).isEqualTo("redirect:/player");
        assertThat(redirectAttributes.getFlashAttributes()).containsKey("errormessage");
        verify(playerService, never()).save(any(Player.class));
    }

    @Test
    void importPlayersSavesOnlyNewPlayers() {
        when(playerService.existsByName("Matti", "Meikalainen")).thenReturn(false);
        when(playerService.existsByName("Liisa", "Mallinen")).thenReturn(true);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.importPlayers("Matti Meikalainen\nLiisa Mallinen", redirectAttributes);

        assertThat(view).isEqualTo("redirect:/player");
        assertThat(redirectAttributes.getFlashAttributes()).containsKey("message");
        verify(playerService).save(any(Player.class));
    }
}
