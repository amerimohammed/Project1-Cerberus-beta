package nl.miwgroningen.c11.cerberus.docentenportaalproject.controller;

import lombok.RequiredArgsConstructor;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Role;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Student;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Teacher;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.User;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.RoleRepository;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Handles all actions concerning users.
 *
 * @author Marianne Kooistra, Mohammed Almameri, Joost Schreuder
 */

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @GetMapping("/changePassword")
    private String showChangePasswordForm() {
        return "/user/changePassword";
    }

    @PostMapping("/changePassword")
    private String changePassword(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");

        if (oldPassword.equals(newPassword)) {
            model.addAttribute("message", "Your new password must be different than the old one.");

            return "user/changePassword";
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            model.addAttribute("message", "Your old password is incorrect.");

            return "user/changePassword";

        } else {
            updateUserPassword(user, newPassword);

            return logoutUserAndRedirect(request, model, redirectAttributes);
        }
    }

    private void updateUserPassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setFirstLogin(false);
        userRepository.save(user);
    }

    private String logoutUserAndRedirect(HttpServletRequest request, Model model,
                                         RedirectAttributes redirectAttributes) {
        try {
            request.logout();
            redirectAttributes.addFlashAttribute("message",
                    "Password changed successfully. Please log again with the new password.");

            return "redirect:/login";
        } catch (ServletException servletException) {
            servletException.printStackTrace();
            model.addAttribute("message", "Your old password is incorrect.");

            return "user/changePassword";
        }
    }

    protected void createUser(User user, Model model) {
        user.generateUsernameAndPassword(userRepository);
        String tempPassword = user.getPassword();
        user.hashPassword();
        if (user instanceof Teacher) {

            addRole(user, "TEACHER");
        } else if (user instanceof Student) {
            addRole(user, "STUDENT");
        }

        user.setFirstLogin(true);
        userRepository.save(user);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("password", tempPassword);
    }

    private void addRole(User user, String roleName) {
        Optional<Role> role = roleRepository.findRoleByRoleName(roleName);
        if (role.isPresent()) {
            Set<Role> userRoles = new HashSet<>();
            userRoles.add(role.get());
            user.setRoles(userRoles);
        }
    }
}
