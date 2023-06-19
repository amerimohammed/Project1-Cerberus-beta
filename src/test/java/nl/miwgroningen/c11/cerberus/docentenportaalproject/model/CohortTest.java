package nl.miwgroningen.c11.cerberus.docentenportaalproject.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CohortTest {

    private static final String TEST_START_DATE_STRING = "01 - 01 - 1999";
    private static final String TEST_END_DATE_STRING = "02 - 01 - 1999";
    private static final LocalDate TEST_DATE = LocalDate.parse("1999-01-01");

    @Test
    void setStartDate() {
        Cohort cohort = new Cohort();

        //Test whether endDate can be set when there is no startDate yet
        assertDoesNotThrow(() -> cohort.setEndDate(TEST_DATE));

        //EndDate and StartDate cannot be the same
        assertThrows(IllegalArgumentException.class, () -> cohort.setStartDate(TEST_DATE));

        //StartDate cannot be after endDate
        assertThrows(IllegalArgumentException.class, () -> cohort.setStartDate(TEST_DATE.plusDays(1)));

        //StartDate cannot be set to null
        assertThrows(NullPointerException.class, () -> cohort.setStartDate(null));

        //StartDate can be set before endDate
        assertDoesNotThrow(() -> cohort.setStartDate(TEST_DATE.minusDays(1)));

        //Check whether date is set right
        assertEquals(TEST_DATE.minusDays(1), cohort.getStartDate());
    }

    @Test
    void setEndDate() {
        Cohort cohort = new Cohort();

        //Test whether endDate can be set when there is no startDate yet
        assertDoesNotThrow(() -> cohort.setStartDate(TEST_DATE));

        //endDate and startDate cannot be on the same day
        assertThrows(IllegalArgumentException.class, () -> cohort.setEndDate(TEST_DATE));

        //endDate cannot be before startDate
        assertThrows(IllegalArgumentException.class, () -> cohort.setEndDate(TEST_DATE.minusDays(1)));

        //endDate cannot be set to null
        assertThrows(NullPointerException.class, () -> cohort.setEndDate(null));

        //endDate can be after startDate
        assertDoesNotThrow(() -> cohort.setEndDate(TEST_DATE.plusDays(1)));

        //Check whether date is set right
        assertEquals(TEST_DATE.plusDays(1), cohort.getEndDate());
    }

    @Test
    void displayDate() {
        Cohort cohort = new Cohort();

        cohort.setStartDate(TEST_DATE);
        cohort.setEndDate(TEST_DATE.plusDays(1));

        assertEquals(TEST_START_DATE_STRING, cohort.displayStartDate());
        assertEquals(TEST_END_DATE_STRING, cohort.displayEndDate());
    }
}