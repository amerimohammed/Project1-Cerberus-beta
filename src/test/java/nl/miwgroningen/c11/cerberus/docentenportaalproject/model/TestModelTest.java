package nl.miwgroningen.c11.cerberus.docentenportaalproject.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing if methods in test model work as they are expected to.
 */
class TestModelTest {

    //This is a constant to avoid having to use the long adres of the Test model.
    //(Next time when working with 'toetsen' give them a different name)
    private static final nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Test TEST = new nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Test();
    private static final LocalDate DATE_ONE = LocalDate.of(2023, 1, 21);
    private static final String DATE_ONE_DISPLAY = "21 - 01 - 2023";
    private static final LocalDate DATE_TWO = LocalDate.of(2000, 1, 1);

    @Test
    @DisplayName("DisplayTestDate shows formatted date")
    void displayDateShowsFormattedDate() {
        TEST.setTestDate(DATE_ONE);

        assertEquals(DATE_ONE_DISPLAY, TEST.displayTestDate());
    }

    @Test
    @DisplayName("DisplayTestDate shows no date when date is null")
    void DisplayDateNoDateWhenNull() {
        setTestToAllNull();
        assertEquals("No date", TEST.displayTestDate());
    }

    @Test
    @DisplayName("If test date of test part is null then inherit from super test")
    void testDateTestPartIsNullInheritFromSuperTest() {
        nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Test test = setUpTestWithTestPart();

        test.inheritFromSuper(test);

        List<nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Test> testParts = test.getTestParts();
        nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Test part = testParts.get(0);

        assertEquals(test.getTestDate(), part.getTestDate());
    }

    @Test
    @DisplayName("If test date of test part is not null, then do not inherit from super test")
    void testDateOfTestPartIsNotNullDoNotInheritFromSuperTest() {
        nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Test test = setUpTestWithTestPart();

        List<nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Test> testParts = test.getTestParts();
        testParts.get(0).setTestDate(DATE_TWO);

        assertNotEquals(test.getTestDate(), testParts.get(0).getTestDate());
    }

    //Method to be able to reuse the same constant
    private static void setTestToAllNull() {
        TEST.setTestDate(null);
        TEST.setSuperTest(null);
        TEST.setTestParts(null);
    }

    private static nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Test setUpTestWithTestPart() {
        setTestToAllNull();
        nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Test subTest = new nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Test();

        List<nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Test> parts = new ArrayList<>();
        parts.add(subTest);

        TEST.setTestDate(DATE_ONE);
        TEST.setTestParts(parts);

        return TEST;
    }
}