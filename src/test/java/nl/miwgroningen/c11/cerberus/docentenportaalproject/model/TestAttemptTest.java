package nl.miwgroningen.c11.cerberus.docentenportaalproject.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DOEL VAN PROGRAMMA HIER!!!!
 *
 * @author Joost Schreuder
 */
class TestAttemptTest {

    @Test
    void SetScore() {
        TestAttempt superTestAttempt = new TestAttempt();

        TestAttempt testAttempt = new TestAttempt();
        TestAttempt testAttemptTwo = new TestAttempt();

        testAttempt.setSuperTestAttempt(superTestAttempt);
        testAttemptTwo.setSuperTestAttempt(superTestAttempt);

        TestAttempt testAttemptPartOne = new TestAttempt();
        TestAttempt testAttemptPartTwo = new TestAttempt();
        TestAttempt testAttemptPartThree = new TestAttempt();


        testAttemptPartOne.setSuperTestAttempt(testAttempt);
        testAttemptPartTwo.setSuperTestAttempt(testAttempt);

        testAttemptPartThree.setSuperTestAttempt(testAttemptTwo);

        List<TestAttempt> subTestAttempts = new ArrayList<>();
        subTestAttempts.add(testAttemptPartOne);
        subTestAttempts.add(testAttemptPartTwo);

        List<TestAttempt> subTestAttemptsTwo = new ArrayList<>();
        subTestAttemptsTwo.add(testAttemptPartThree);

        testAttempt.setSubTestAttempts(subTestAttempts);
        testAttemptTwo.setSubTestAttempts(subTestAttemptsTwo);

        List<TestAttempt> subTestAttemptForSuperAttempt = new ArrayList<>();

        subTestAttemptForSuperAttempt.add(testAttempt);
        subTestAttemptForSuperAttempt.add(testAttemptTwo);

        superTestAttempt.setSubTestAttempts(subTestAttemptForSuperAttempt);

        testAttemptPartOne.setScore(20);
        testAttemptPartTwo.setScore(20);

        assertEquals(40, testAttempt.getScore());

        testAttemptPartThree.setScore(20);

        assertEquals(60, superTestAttempt.getScore());
    }
}