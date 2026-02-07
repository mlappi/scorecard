package fi.mlappi.golf.controller;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @InjectMocks
    private AdminController controller;

    @Test
    void loginSuccessSetsSessionFlagAndRedirectsHome() {
        ReflectionTestUtils.setField(controller, "adminUsername", "admin");
        ReflectionTestUtils.setField(controller, "adminPassword", "secret");
        MockHttpSession session = new MockHttpSession();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.doLogin("admin", "secret", session, redirectAttributes);

        assertThat(view).isEqualTo("redirect:/");
        assertThat(session.getAttribute("isAdmin")).isEqualTo(Boolean.TRUE);
        assertThat(redirectAttributes.getFlashAttributes()).isEmpty();
    }

    @Test
    void loginFailureAddsFlashErrorAndRedirectsToLogin() {
        ReflectionTestUtils.setField(controller, "adminUsername", "admin");
        ReflectionTestUtils.setField(controller, "adminPassword", "secret");
        MockHttpSession session = new MockHttpSession();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.doLogin("wrong", "creds", session, redirectAttributes);

        assertThat(view).isEqualTo("redirect:/admin/login");
        assertThat(session.getAttribute("isAdmin")).isNull();
        assertThat(redirectAttributes.getFlashAttributes())
                .containsKey("error");
    }

    @Test
    void logoutInvalidatesSessionAndRedirectsHome() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("isAdmin", Boolean.TRUE);

        String view = controller.logout(session);

        assertThat(view).isEqualTo("redirect:/");
        assertThat(session.isInvalid()).isTrue();
    }
}
