package nl.miwgroningen.c11.cerberus.docentenportaalproject.model;

import lombok.Getter;
import lombok.Setter;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.UserRepository;
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

    public void generateUsernameAndPassword(UserRepository userRepository) {
        password = new Random().ints(10, 33, 122)
                .mapToObj(i -> String.valueOf((char) i)).collect(Collectors.joining());

        String[] firstNameLastName = fullName.split(" ");
        username = String.valueOf(firstNameLastName[0].charAt(0));
        if(firstNameLastName.length > 1){
            username = username + "." + firstNameLastName[1];
        }
        username = username.toLowerCase();

        Optional<User> optionalUser = userRepository.findByUsername(username);

        Integer counter = 2;
        StringBuilder usernameBuilder = new StringBuilder(username + "." + counter);
        while (optionalUser.isPresent()) {
            username = usernameBuilder.toString();
            optionalUser = userRepository.findByUsername(username);
            counter++;
            usernameBuilder.deleteCharAt(usernameBuilder.length() - 1).append(counter);
        }
    }

    public void hashPassword() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        password = encoder.encode(password);
    }
}
