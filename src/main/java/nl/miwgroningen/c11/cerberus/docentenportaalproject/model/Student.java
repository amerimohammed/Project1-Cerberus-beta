package nl.miwgroningen.c11.cerberus.docentenportaalproject.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

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
        // Latest cohort shown first in the list
        if (cohort.getCohortId() > otherStudent.cohort.getCohortId()) {
            comparedCohort = -1;
        } else if (cohort.getCohortId() < otherStudent.cohort.getCohortId()) {
            comparedCohort = 1;
        } else {
            comparedCohort = 0;
        }

        if (comparedCohort != 0) {
            return comparedCohort;
        }
        return fullName.compareToIgnoreCase(otherStudent.fullName);
    }
}
