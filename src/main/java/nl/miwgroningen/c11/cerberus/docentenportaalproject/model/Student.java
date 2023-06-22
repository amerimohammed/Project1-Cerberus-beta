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
@Getter @Setter
public class Student extends User {

    @ManyToOne
    private Cohort cohort;

    @OneToMany(mappedBy="student")
    private List<TestAttempt> testAttempts;

    public String displayNameAndId() {
        return String.format("%d: %s", userId, fullName);
    }
}
