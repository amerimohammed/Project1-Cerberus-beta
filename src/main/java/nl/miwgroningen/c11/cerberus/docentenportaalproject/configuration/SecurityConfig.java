package nl.miwgroningen.c11.cerberus.docentenportaalproject.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration to handle access roles to the app resources
 *
 * @author Marianne Kooistra, Mohammed Almameri, Joost Schreuder 15/06/2023
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests(authorize -> authorize
                .antMatchers("/css/**", "/webjars/**", "/images/**", "/js/**").permitAll()
                .antMatchers("/").permitAll()
                .anyRequest().authenticated()
        )
                .formLogin().and().logout().logoutSuccessUrl("/");
        return httpSecurity.build();
    }
}
