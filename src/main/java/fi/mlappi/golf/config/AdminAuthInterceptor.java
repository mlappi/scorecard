package fi.mlappi.golf.config;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final boolean adminEnabled;

    public AdminAuthInterceptor(@Value("${app.admin.enabled:true}") boolean adminEnabled) {
        this.adminEnabled = adminEnabled;
    }

    private static final List<String> ALLOWED_POST_PATHS = List.of(
            "/player/search",
            "/game/search"
    );

    private static final List<String> BLOCKED_GET_PREFIXES = List.of(
            "/game/new",
            "/game/edit",
            "/game/addRound",
            "/game/remove",
            "/game/remove-round",
            "/course/new",
            "/course/edit",
            "/course/remove",
            "/player/new",
            "/player/edit",
            "/player/remove",
            "/player/import",
            "/score/add",
            "/score/edit",
            "/score/remove",
            "/score/import"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        String path = request.getRequestURI();
        if (path == null) {
            return true;
        }
        if (path.startsWith("/admin/login") || path.startsWith("/admin/logout")) {
            return true;
        }
        if (path.equals("/") || path.startsWith("/app.css") || path.startsWith("/error")
                || path.startsWith("/webjars") || path.startsWith("/images") || path.startsWith("/favicon")) {
            return true;
        }
        if (!adminEnabled) {
            if (!"GET".equalsIgnoreCase(request.getMethod()) && !ALLOWED_POST_PATHS.contains(path)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
            for (String prefix : BLOCKED_GET_PREFIXES) {
                if (path.startsWith(prefix)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return false;
                }
            }
            return true;
        }
        HttpSession session = request.getSession(false);
        boolean isAdmin = session != null && Boolean.TRUE.equals(session.getAttribute("isAdmin"));
        if (isAdmin) {
            return true;
        }
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            for (String allowed : ALLOWED_POST_PATHS) {
                if (path.equals(allowed)) {
                    return true;
                }
            }
            response.sendRedirect("/admin/login");
            return false;
        }
        for (String prefix : BLOCKED_GET_PREFIXES) {
            if (path.startsWith(prefix)) {
                response.sendRedirect("/admin/login");
                return false;
            }
        }
        return true;
    }
}
