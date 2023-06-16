package nl.miwgroningen.c11.cerberus.docentenportaalproject.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * a person who has an account on the app
 *
 * @author Marianne Kooistra, Mohammed Almameri, Joost Schreuder 15/06/2023
 */

@Entity
@Setter
@Getter
public class User implements UserDetails {
    @Id
    @GeneratedValue
    protected Long userId;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;
    private boolean enabled = true;

    protected String fullName;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        for (Role role : roles) {
            authorityList.add(new SimpleGrantedAuthority(role.getRoleName()));
        }

        System.out.println(authorityList);
        return authorityList;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void generateUsernameAndPassword() {
        password = new Random().ints(10, 33, 122)
                .mapToObj(i -> String.valueOf((char) i)).collect(Collectors.joining());

        username = this.fullName.substring(0, fullName.indexOf(' ')).toLowerCase();
    }

    public void hashPassword() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        password = encoder.encode(password);
    }
}
