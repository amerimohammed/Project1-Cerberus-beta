package nl.miwgroningen.c11.cerberus.docentenportaalproject.filter;

import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * to check if the user logged in for the first time and force him to change password
 *
 * @author Marianne Kooistra, Mohammed Almameri, Joost Schreuder 21/06/2023
 */
@Component
public class FirstLoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if (allowedUrl(httpRequest)) {
            filterChain.doFilter(request, response);
            return;
        }

        User user = getLoggedInUser();

        if (user != null && user.isFirstLogin()) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/user/changePassword");
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = null;

        if (authentication != null) {
            principal = authentication.getPrincipal();
        }

        if (principal instanceof User) {
            return (User) principal;
        }
        return null;
    }

    private boolean allowedUrl(HttpServletRequest httpRequest) {
        String url = httpRequest.getRequestURI();
        if (url.endsWith("/user/changePassword") || url.endsWith(".css") || url.endsWith(".js") || url.endsWith(".map")) {
            return true;
        }
        return false;
    }

}
