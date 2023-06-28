package nl.miwgroningen.c11.cerberus.docentenportaalproject.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing if methods in test model work as they are expected to.
 */
class TestModelTest {

    //This is a constant to avoid having to use the long adres of the Test model.
    //(Next time when working with 'toetsen' give them a different name)
    private static final nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Test TEST = new nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Test();

    @Test
    @DisplayName("DisplayTestDate shows formatted date")
    void displayDateShowsFormattedDate() {
        TEST.setTestDate(LocalDate.of(2023,01, 21));

        assertEquals("21 - 01 - 2023", TEST.displayTestDate());
    }

    @Test
    @DisplayName("DisplayTestDate shows no date when date is null")
    void DisplayDateNoDateWhenNull() {
        setTestToAllNull();
        assertEquals("No date", TEST.displayTestDate());
    }


    //Method to be able to reuse the same constant
    private static void setTestToAllNull() {
        TEST.setTestDate(null);
    }

}