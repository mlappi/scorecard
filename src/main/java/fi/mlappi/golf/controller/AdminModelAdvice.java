package fi.mlappi.golf.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class AdminModelAdvice {

    @ModelAttribute("isAdmin")
    public boolean isAdmin(HttpSession session) {
        Object value = session.getAttribute("isAdmin");
        return Boolean.TRUE.equals(value);
    }
}
