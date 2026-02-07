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

    private final AdminModelAdvice advice = new AdminModelAdvice();

    @Mock
    private HttpSession session;

    @Test
    void isAdminReturnsTrueWhenSessionFlagSet() {
        when(session.getAttribute("isAdmin")).thenReturn(Boolean.TRUE);

        boolean result = advice.isAdmin(session);

        assertThat(result).isTrue();
    }

    @Test
    void isAdminReturnsFalseWhenSessionFlagMissing() {
        when(session.getAttribute("isAdmin")).thenReturn(null);

        boolean result = advice.isAdmin(session);

        assertThat(result).isFalse();
    }
}
