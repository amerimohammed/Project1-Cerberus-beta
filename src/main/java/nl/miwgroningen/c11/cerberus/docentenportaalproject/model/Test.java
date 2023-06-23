package nl.miwgroningen.c11.cerberus.docentenportaalproject.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

/**
 * A type of assignment that occurs at a set date and serves as an evaluation moment, to evaluate the learned skills of a student in a subject.
 *
 * @author Marianne Kooistra, Mohammed Almameri, Joost Schreuder
 */

@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Test extends Assignment {

    private String testName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate testDate;

    @ManyToOne
    private Test superTest;

    @OneToMany(mappedBy = "superTest", cascade = CascadeType.ALL)
    private List<Test> testParts;

    private String description;

    @OneToMany(mappedBy = "test")
    private List<TestAttempt> testAttempts;

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
