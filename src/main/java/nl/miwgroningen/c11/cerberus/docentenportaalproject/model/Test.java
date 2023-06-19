package nl.miwgroningen.c11.cerberus.docentenportaalproject.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * An evaluation moment, to evaluate the learned skills of a student in a subject.
 *
 * @author Marianne Kooistra, Mohammed Almameri, Joost Schreuder
 */

@Entity
@Getter @Setter
public class Test {

    @Id
    @GeneratedValue
    private Long testId;

    private String testName;

    private LocalDate testDate;

    @ManyToOne
    private Subject subject;
}
