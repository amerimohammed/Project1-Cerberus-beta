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
@Getter @Setter
public class Student {

    @Id
    @GeneratedValue
    private Long studentId;

    private String studentName;

    @ManyToOne
    private Cohort cohort;
}
