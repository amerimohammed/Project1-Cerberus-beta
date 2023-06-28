package nl.miwgroningen.c11.cerberus.docentenportaalproject.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A person in a cohort.
 *
 * @author Marianne Kooistra, Mohammed Almameri, Joost Schreuder
 */

@Entity
@Getter
@Setter
public class Student extends User implements Comparable<Student> {

    @ManyToOne
    private Cohort cohort;

    @OneToMany(mappedBy = "student")
    private List<TestAttempt> testAttempts;

    public String displayNameAndId() {
        return String.format("%d: %s", userId, fullName);
    }

    @Override
    public int compareTo(Student otherStudent) {
        int comparedCohort;

        // First unknowns in list
        if (cohort == null) {
            comparedCohort = -1;
        } else if (otherStudent.cohort == null) {
            comparedCohort = 1;
        } else {
            // Latest cohort shown first in the list
            comparedCohort = otherStudent.cohort.getCohortId().compareTo(cohort.getCohortId());
        }

        if (comparedCohort != 0) {
            return comparedCohort;
        }
        return fullName.compareToIgnoreCase(otherStudent.fullName);
    }

    public List<TestAttempt> getTestAttemptsForSpecificTest(Test test) {
        List<TestAttempt> testAttemptsForSpecificTest = new ArrayList<>();

        for (TestAttempt testAttempt : testAttempts) {
            if (testAttempt.getTest() == test) {
                testAttemptsForSpecificTest.add(testAttempt);
            }
        }

        return testAttemptsForSpecificTest;
    }

    public Set<Test> getAllSuperTestAttempts() {
        Set<Test> testSet = new HashSet<>();

        for (TestAttempt testAttempt : testAttempts) {
            if (testAttempt.getSuperTestAttempt() == null) {
                testSet.add(testAttempt.getTest());
            }
        }

        return testSet;
    }
}
