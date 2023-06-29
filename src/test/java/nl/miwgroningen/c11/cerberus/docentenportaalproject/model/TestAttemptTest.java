package nl.miwgroningen.c11.cerberus.docentenportaalproject.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for TestAttempt methods.
 */

class TestAttemptTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 4, 10, 15, 1234, Integer.MAX_VALUE})
    @DisplayName("Setting scores to positive values should be possible")
    void setScorePositive(int score) {
        TestAttempt testAttempt = new TestAttempt();

        assertDoesNotThrow(() -> testAttempt.setScore(score));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -4, -10, -15, -1234, Integer.MIN_VALUE})
    @DisplayName("Setting scores to negative values should not be possible")
    void setScoreNegative() {
        TestAttempt testAttempt = new TestAttempt();

        assertThrows(IllegalArgumentException.class, () -> testAttempt.setScore(-1));
    }

    @Test
    @DisplayName("Setting score to zero should be possible")
    void setScoreZero() {
        TestAttempt testAttempt = new TestAttempt();

        assertDoesNotThrow(() -> testAttempt.setScore(0));
    }

    @Test
    @DisplayName("superTestAttempt should be set to graded if all subTestAttempts are graded")
    void setIsGradedSuperTestWhenSubTestsAreGraded() {
        TestAttempt testAttempt = new TestAttempt();

        TestAttempt testAttemptPartOne = new TestAttempt();
        TestAttempt testAttemptPartTwo = new TestAttempt();
        testAttemptPartOne.setSuperTestAttempt(testAttempt);
        testAttemptPartTwo.setSuperTestAttempt(testAttempt);
        List<TestAttempt> subTestAttempts = new ArrayList<>();
        subTestAttempts.add(testAttemptPartOne);
        subTestAttempts.add(testAttemptPartTwo);
        testAttempt.setSubTestAttempts(subTestAttempts);

        testAttemptPartOne.setIsGraded(true);
        testAttemptPartTwo.setIsGraded(true);

        testAttempt.setIsGraded(false);

        assertTrue(testAttempt.isGraded());
    }

    @Test
    @DisplayName("superTestAttempt should not be set to graded if not all subTestAttempts are graded yet")
    void setIsGradedSuperTestWhenNotAllSubTestsAreGraded() {
        TestAttempt testAttempt = new TestAttempt();

        TestAttempt testAttemptPartOne = new TestAttempt();
        TestAttempt testAttemptPartTwo = new TestAttempt();
        testAttemptPartOne.setSuperTestAttempt(testAttempt);
        testAttemptPartTwo.setSuperTestAttempt(testAttempt);
        List<TestAttempt> subTestAttempts = new ArrayList<>();
        subTestAttempts.add(testAttemptPartOne);
        subTestAttempts.add(testAttemptPartTwo);
        testAttempt.setSubTestAttempts(subTestAttempts);

        testAttemptPartOne.setIsGraded(true);
        testAttemptPartTwo.setIsGraded(false);

        testAttempt.setIsGraded(true);

        assertFalse(testAttempt.isGraded());
    }
}