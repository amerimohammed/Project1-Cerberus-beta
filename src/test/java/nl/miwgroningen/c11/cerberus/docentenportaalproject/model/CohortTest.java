package nl.miwgroningen.c11.cerberus.docentenportaalproject.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Cohort methods.
 */

class CohortTest {

    private static final String TEST_START_DATE_STRING = "01 - 01 - 1999";
    private static final String TEST_END_DATE_STRING = "02 - 01 - 1999";
    private static final LocalDate TEST_DATE = LocalDate.parse("1999-01-01");

    @Test
    @DisplayName("Setting startDate without endDate should be possible")
    void setStartDateWithoutEndDate() {
        Cohort cohort = new Cohort();

        assertDoesNotThrow(() -> cohort.setStartDate(TEST_DATE));
    }

    @Test
    @DisplayName("setting startDate before endDate should be possible")
    void setStartDateBeforeEndDate() {
        Cohort cohort = setupCohortWithEndDate();

        assertDoesNotThrow(() -> cohort.setStartDate(TEST_DATE.minusDays(1)));
    }

    @Test
    @DisplayName("Setting startDate on same day as endDate should result in an error")
    void setStartDateOnSameDayAsEndDate() {
        Cohort cohort = setupCohortWithEndDate();

        assertThrows(IllegalArgumentException.class, () -> cohort.setStartDate(TEST_DATE));
    }

    @Test
    @DisplayName("Setting startDate after endDate should result in an error")
    void setStartDateAfterEndDate() {
        Cohort cohort = setupCohortWithEndDate();

        assertThrows(IllegalArgumentException.class, () -> cohort.setStartDate(TEST_DATE.plusDays(1)));
    }

    @Test
    @DisplayName("Setting endDate without startDate should be possible")
    void setEndDateWithoutStartDate() {
        Cohort cohort = new Cohort();

        assertDoesNotThrow(() -> cohort.setEndDate(TEST_DATE));
    }

    @Test
    @DisplayName("Setting endDate after StartDate should be possible")
    void setEndDateAfterStartDate() {
        Cohort cohort = setupCohortWithStartDate();

        assertDoesNotThrow(() -> cohort.setEndDate(TEST_DATE.plusDays(1)));
    }

    @Test
    @DisplayName("Setting endDate on same day as startDay should result in an error")
    void setEndDateOnSameDayAsStartDate() {
        Cohort cohort = setupCohortWithStartDate();

        assertThrows(IllegalArgumentException.class, () -> cohort.setEndDate(TEST_DATE));
    }

    @Test
    @DisplayName("Setting endDate before StartDate should result in an error")
    void setEndDateBeforeStartDate() {
        Cohort cohort = setupCohortWithStartDate();

        assertThrows(IllegalArgumentException.class, () -> cohort.setEndDate(TEST_DATE.minusDays(1)));
    }

    private static Cohort setupCohortWithStartDate() {
        Cohort cohort = new Cohort();
        cohort.setStartDate(TEST_DATE);
        return cohort;
    }

    private static Cohort setupCohortWithEndDate() {
        Cohort cohort = new Cohort();
        cohort.setEndDate(TEST_DATE);
        return cohort;
    }

    @Test
    @DisplayName("Dates are displayed correctly")
    void displayDate() {
        Cohort cohort = setupCohortWithStartDate();
        cohort.setEndDate(TEST_DATE.plusDays(1));

        assertEquals(TEST_START_DATE_STRING, cohort.displayStartDate());
        assertEquals(TEST_END_DATE_STRING, cohort.displayEndDate());
    }
}