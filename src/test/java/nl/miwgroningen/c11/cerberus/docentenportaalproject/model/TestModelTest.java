package nl.miwgroningen.c11.cerberus.docentenportaalproject.model;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing if methods in test model work as they are expected to.
 */
class TestModelTest {

    @Test
    @DisplayName("DisplayTestDate shows no date when date is null")
    void DisplayDateNoDateWhenNull() {
        nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Test test = new nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Test();
        test.setTestDate(null);

        assertEquals("No date", test.displayTestDate());
    }

    //TODO: invalid date test-> month 13 + day 31 of february + day 32 of any other month.
    @Test
    @Disabled
    @DisplayName("DisplayTestDate gives error when date is invalid")
    void displayDateGivesErrorWhenDateInvalid() {
        nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Test test = new nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Test();
        test.setTestDate(LocalDate.of(2023, 13,1));

    }

    @Test
    @DisplayName("DisplayTestDate shows formatted date")
    void displayDateShowsFormattedDate() {
        nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Test test = new nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Test();
        test.setTestDate(LocalDate.of(2023, 01, 21));

        assertEquals("21 - 01 - 2023", test.displayTestDate());
    }
}