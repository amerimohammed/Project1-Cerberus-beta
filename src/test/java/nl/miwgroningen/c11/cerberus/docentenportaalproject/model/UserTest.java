package nl.miwgroningen.c11.cerberus.docentenportaalproject.model;

import nl.miwgroningen.c11.cerberus.docentenportaalproject.DocentenportaalProjectApplication;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for User.
 */

@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest(classes = DocentenportaalProjectApplication.class)
class UserTest {
    @Autowired
    UserRepository userRepository;
    User user = new User();

    // Testing setFullName()
    @ParameterizedTest
    @DisplayName("setting full name with only spaces, special characters or less than two letters should throw an error")
    @ValueSource(strings = {"  ", "A", "\t", "\n"})
    void settingNameWithSpacesOrSpecialCharacterOrLessThanTwoLettersShouldThrowError(String illegalName) {
        assertThrows(IllegalArgumentException.class, () -> user.setFullName(illegalName));
    }

    @ParameterizedTest
    @DisplayName("Setting full name with two letters or more should not throw no errors")
    @ValueSource(strings = {"Le", "John", "Mark van de Vries"})
    void settingNameWithTwoLettersShouldNotThrowError(String legalName) {
        assertDoesNotThrow(() -> user.setFullName(legalName));
    }

    // Testing generateUsernameAndPassword()
    @Test
    @DisplayName("Setting full name with 'admin' should not make 'admin' username")
    void settingNameWithAdminShouldNotMakeUsernameAdmin() {
        user.setFullName("admin");
        user.generateUsernameAndPassword(userRepository);
        assertNotEquals("admin", user.getUsername());
    }

    @Test
    @DisplayName("Setting full name with only the first name should make user name with the whole name")
    void settingNameWithOnlyFirstNameShouldMakeUsernameWithWholeName() {
        user.setFullName("John");
        user.generateUsernameAndPassword(userRepository);
        assertTrue(user.getUsername().startsWith("john"));
    }

    @Test
    @DisplayName("Setting full name with firstname and lastname should make username with first letter of firstname, dot and lastname")
    void settingNameWithFirstNameLastNameShouldMakeUsernameWithFirstLetterDotLastname() {
        user.setFullName("John Doe");
        user.generateUsernameAndPassword(userRepository);
        assertTrue(user.getUsername().startsWith("j.doe"));
    }

    @Test
    @DisplayName("Setting  full name of user with the same full name of already saved one should make username different than old one")
    void settingNameWithSameNameOfOtherOldUserShouldMakeDifferentUsername() {
        user.setFullName("John Doe");
        user.generateUsernameAndPassword(userRepository);
        userRepository.save(user);
        user.setFullName("John Doe");
        user.generateUsernameAndPassword(userRepository);
        assertNotEquals("j.doe", user.getUsername());
    }

    @Test
    @DisplayName("Setting full name with firstname middle name and lastname should make username with first letter, dot and lastname")
    void settingNameWithFirstNameMiddleNameLastNameShouldMakeUsernameWithFirstLetterDotLastname() {
        user.setFullName("Mark van de Vries");
        user.generateUsernameAndPassword(userRepository);
        assertTrue(user.getUsername().startsWith("m.vries"));
    }
}