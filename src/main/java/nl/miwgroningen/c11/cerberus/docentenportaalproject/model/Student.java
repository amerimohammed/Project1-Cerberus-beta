package nl.miwgroningen.c11.cerberus.docentenportaalproject.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * A person in a cohort.
 *
 * @author Marianne Kooistra, Mohammed Almameri, Joost Schreuder
 */

@Entity
@Getter
@Setter
public class Student extends User {
    @ManyToOne
    private Cohort cohort;

    public String displayNameAndId() {
        return String.format("%d: %s", userId, fullName);
    }
}
