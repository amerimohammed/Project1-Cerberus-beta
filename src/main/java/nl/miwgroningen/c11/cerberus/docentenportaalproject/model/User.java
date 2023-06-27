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
@Getter
@Setter
public class User implements UserDetails {

    public static final int PASSWORD_LENGTH = 10;
    public static final int ASCII_RANGE_MIN = 33;
    public static final int ASCII_RANGE_MAX = 122;
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

    protected boolean firstLogin = false;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        for (Role role : roles) {
            authorityList.add(new SimpleGrantedAuthority(role.getRoleName()));
        }

        System.out.println(authorityList);
        return authorityList;
    }

    public void setFullName(String fullName) {
        fullName = fullName.trim();
        if (fullName.length() < 2) {
            throw new IllegalArgumentException("Name must be at least 2 characters.");
        }
        this.fullName = fullName;
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
        username = generateUsername(userRepository);
        password = generateRandomPassword();
    }

    public void hashPassword() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        password = encoder.encode(password);
    }

    private String generateRandomPassword() {
        return new Random().ints(PASSWORD_LENGTH, ASCII_RANGE_MIN, ASCII_RANGE_MAX)
                .mapToObj(i -> String.valueOf((char) i)).collect(Collectors.joining());
    }

    private String generateUsername(UserRepository userRepository) {
        StringBuilder usernameBuilder = new StringBuilder();
        String[] firstNameLastName = fullName.split(" ");

        if (firstNameLastName.length > 1) {
            usernameBuilder.append(firstNameLastName[0].charAt(0))
                    .append(".").append(firstNameLastName[firstNameLastName.length - 1]);
        } else {
            usernameBuilder.append(fullName);
        }

        String username = usernameBuilder.toString().toLowerCase();
        Optional<User> optionalUser = userRepository.findByUsername(username);

        Integer counter = 2;
        usernameBuilder.append(".").append(counter);

        while (optionalUser.isPresent()) {
            username = usernameBuilder.toString();
            optionalUser = userRepository.findByUsername(username);
            counter++;
            usernameBuilder.deleteCharAt(usernameBuilder.length() - 1).append(counter);
        }
        return username;
    }
}
