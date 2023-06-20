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

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate testDate;

    @ManyToOne
    private Subject subject;

    public String displayTestDate() {
        String display;
        if (testDate == null) {
            display = "No date";
        } else {
            display = String.format("%02d - %02d - %4d", testDate.getDayOfMonth(), testDate.getMonthValue(), testDate.getYear());
        }

        return display;
    }
}
