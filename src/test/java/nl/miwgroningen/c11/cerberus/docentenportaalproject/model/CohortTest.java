package nl.miwgroningen.c11.cerberus.docentenportaalproject.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
/**
    * DOEL VAN PROGRAMMA HIER!!!!
    *
    * @author Joost Schreuder
*/class CohortTest {

    private static final LocalDate TEST_DATE = LocalDate.parse("1999-01-01");

    //Tests whether error is thrown when end-date is before start-date
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
        assertEquals(cohort.getStartDate(), TEST_DATE.minusDays(1));
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
        assertEquals(cohort.getEndDate(), TEST_DATE.plusDays(1));
    }
}