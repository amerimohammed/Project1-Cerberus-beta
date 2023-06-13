package nl.miwgroningen.c11.cerberus.docentenportaalproject.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A group of students following the same programme at the same time.
 *
 * @author Marianne Kooistra, Mohammed Almameri, Joost Schreuder
 */

@Entity
@Getter @Setter
public class Cohort {

    @Id
    @GeneratedValue
    private Long cohortId;

    private String cohortName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @OneToMany(mappedBy = "cohort", cascade = CascadeType.PERSIST)
    private List<Student> students = new ArrayList<>();

    public String displayStartDate() {
        return String.format("%2d - %2d - %4d", startDate.getDayOfMonth(), startDate.getMonthValue(), startDate.getYear());
    }

    public String displayEndDate() {
        return String.format("%2d - %2d - %4d", endDate.getDayOfMonth(), endDate.getMonthValue(), endDate.getYear());
    }
}
