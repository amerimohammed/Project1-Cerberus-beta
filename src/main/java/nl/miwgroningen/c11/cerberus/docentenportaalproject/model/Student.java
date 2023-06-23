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
        // Latest cohort shown first in the list
        int comparedCohort = otherStudent.cohort.getCohortId().compareTo(cohort.getCohortId());

        if (comparedCohort != 0) {
            return comparedCohort;
        }
        return fullName.compareToIgnoreCase(otherStudent.fullName);
    }
}
