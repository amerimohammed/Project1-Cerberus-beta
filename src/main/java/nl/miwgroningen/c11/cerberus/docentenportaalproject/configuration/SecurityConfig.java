package nl.miwgroningen.c11.cerberus.docentenportaalproject.configuration;

import lombok.RequiredArgsConstructor;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration to handle access roles to the app resources
 *
 * @author Marianne Kooistra, Mohammed Almameri, Joost Schreuder 15/06/2023
 */

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserService userService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests(authorize -> authorize
                        .antMatchers("/css/**", "/webjars/**", "/images/**", "/js/**", "/photos/**").permitAll()
                        .antMatchers("/", "/home", "/programme/view/**").permitAll()
                        .antMatchers("/teacher/**", "/seed").hasAuthority("ADMIN")
                        .anyRequest().hasAnyAuthority("ADMIN", "TEACHER")
                )
                .formLogin().and().logout().logoutSuccessUrl("/");
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
}
