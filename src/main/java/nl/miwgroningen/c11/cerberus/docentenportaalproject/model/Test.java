package nl.miwgroningen.c11.cerberus.docentenportaalproject.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * An evaluation moment, to evaluate the learned skills of a student in a subject.
 *
 * @author Marianne Kooistra, Mohammed Almameri, Joost Schreuder
 */

@Entity
@Builder
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Test {

    @Id
    @GeneratedValue
    private Long testId;

    private String testName;

    private LocalDate testDate;

    @ManyToOne
    private Subject subject;
}
