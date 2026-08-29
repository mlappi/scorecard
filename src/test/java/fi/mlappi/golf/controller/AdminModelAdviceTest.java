package fi.mlappi.golf.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminModelAdviceTest {

    @Mock
    private HttpSession session;

    @Test
    void isAdminReturnsTrueWhenSessionFlagSet() {
        AdminModelAdvice advice = new AdminModelAdvice(true);
        when(session.getAttribute("isAdmin")).thenReturn(Boolean.TRUE);

        boolean result = advice.isAdmin(session);

        assertThat(result).isTrue();
    }

    @Test
    void isAdminReturnsFalseWhenSessionFlagMissing() {
        AdminModelAdvice advice = new AdminModelAdvice(true);
        when(session.getAttribute("isAdmin")).thenReturn(null);

        boolean result = advice.isAdmin(session);

        assertThat(result).isFalse();
    }

    @Test
    void disabledAdminIgnoresSessionFlag() {
        AdminModelAdvice advice = new AdminModelAdvice(false);

        assertThat(advice.isAdminEnabled()).isFalse();
        assertThat(advice.isAdmin(session)).isFalse();
    }
}
