package fi.mlappi.golf.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class AdminModelAdvice {

    private final boolean adminEnabled;

    public AdminModelAdvice(@Value("${app.admin.enabled:true}") boolean adminEnabled) {
        this.adminEnabled = adminEnabled;
    }

    @ModelAttribute("adminEnabled")
    public boolean isAdminEnabled() {
        return adminEnabled;
    }

    @ModelAttribute("isAdmin")
    public boolean isAdmin(HttpSession session) {
        Object value = adminEnabled ? session.getAttribute("isAdmin") : null;
        return Boolean.TRUE.equals(value);
    }
}
