package nl.miwgroningen.c11.cerberus.docentenportaalproject.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
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

    private LocalDate startDate;

    private LocalDate endDate;

    @OneToMany(mappedBy = "cohort")
    private List<Student> students = new ArrayList<>();

}
