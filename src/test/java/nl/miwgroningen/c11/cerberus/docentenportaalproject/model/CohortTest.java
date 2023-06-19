package nl.miwgroningen.c11.cerberus.docentenportaalproject.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
/**
    * DOEL VAN PROGRAMMA HIER!!!!
    *
    * @author Joost Schreuder
*/class CohortTest {

    //Tests whether error is thrown when end-date is before start-date
    @Test
    void setStartDate() {
        Cohort cohort = new Cohort();

        //Test whether end-date can be set when there is no start-date yet
        assertDoesNotThrow(() -> cohort.setEndDate(LocalDate.parse("1999-01-01")));

        //Test for end-date and start-date on same day
        assertThrows(IllegalArgumentException.class, () -> cohort.setStartDate(LocalDate.parse("1999-01-01")));

        //Test for setting start-date after end-date
        assertThrows(IllegalArgumentException.class, () -> cohort.setStartDate(LocalDate.parse("1999-01-02")));

        //Test for setting start-date to null
        assertThrows(NullPointerException.class, () -> cohort.setStartDate(null));

        //Test whether no error is thrown if start-date is set before end-date
        assertDoesNotThrow(() -> cohort.setStartDate(LocalDate.parse("1998-12-31")));

        //Check whether date is set right
        assertEquals(cohort.getStartDate(), LocalDate.parse("1998-12-31"));
    }

    @Test
    void setEndDate() {
        Cohort cohort = new Cohort();

        //Test whether end-date can be set when there is no start-date yet
        assertDoesNotThrow(() -> cohort.setStartDate(LocalDate.parse("1999-01-02")));

        //Test for end-date and start-date on same day
        assertThrows(IllegalArgumentException.class, () -> cohort.setEndDate(LocalDate.parse("1999-01-02")));

        //Test for setting end-date before start-date
        assertThrows(IllegalArgumentException.class, () -> cohort.setEndDate(LocalDate.parse("1999-01-01")));

        //Test for setting end-date to null
        assertThrows(NullPointerException.class, () -> cohort.setEndDate(null));

        //Test whether no error is thrown if end-date is set after start-date
        assertDoesNotThrow(() -> cohort.setEndDate(LocalDate.parse("1999-01-03")));

        //Check whether date is set right
        assertEquals(cohort.getEndDate(), LocalDate.parse("1999-01-03"));
    }
}