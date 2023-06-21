package nl.miwgroningen.c11.cerberus.docentenportaalproject.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
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
@NoArgsConstructor
public class Cohort implements Comparable<Cohort> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cohortId;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @OneToMany(mappedBy = "cohort")
    private List<Student> students = new ArrayList<>();

    @ManyToOne
    private Programme programme;

    public Cohort(LocalDate startDate, LocalDate endDate) {
        setStartDate(startDate);
        setEndDate(endDate);
    }

    public void addStudentToCohort(Student student) {
        students.add(student);
    }

    public String displayStartDate() {
        return String.format("%02d - %02d - %4d", startDate.getDayOfMonth(), startDate.getMonthValue(), startDate.getYear());
    }

    public String displayEndDate() {
        return String.format("%02d - %02d - %4d", endDate.getDayOfMonth(), endDate.getMonthValue(), endDate.getYear());
    }

    @Override
    public int compareTo(Cohort otherCohort) {
        return cohortId.compareTo(otherCohort.cohortId);
    }

    public void setStartDate(LocalDate startDate) {
        if(endDate == null || startDate.isBefore(endDate)) {
            this.startDate = startDate;
        }
        else throw new IllegalArgumentException("startDate cannot be after endDate");
    }

    public void setEndDate(LocalDate endDate) {
        if(startDate == null || endDate.isAfter(startDate)) {
            this.endDate = endDate;
        }
        else throw new IllegalArgumentException("endDate cannot be before startDate");
    }

    public int getStudentCount() {
        return students.size();
    }
}
