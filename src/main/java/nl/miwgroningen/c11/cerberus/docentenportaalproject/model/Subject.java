package nl.miwgroningen.c11.cerberus.docentenportaalproject.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Part of a programme, consisting of a number of learning units.
 *
 * @author Marianne Kooistra, Mohammed Almameri, Joost Schreuder
 */

@Entity
@Getter @Setter
public class Subject implements Comparable<Subject>{

    @Id
    @GeneratedValue
    private Long subjectId;

    private String subjectName;

    private int durationWeeks;

    @ManyToMany(mappedBy = "subjects")
    private List<Programme> programmes = new ArrayList<>();

    @ManyToMany
    private List<Teacher> teachers = new ArrayList<>();

    @OneToMany(mappedBy = "subject")
    private List<Assignment> assignments = new ArrayList<>();

    public void removeTeacher(Teacher teacher) {
        teachers.remove(teacher);
    }

    public void addTeacher(Teacher teacher) {
        teachers.add(teacher);
    }

    @Override
    public int compareTo(Subject otherSubject) {
        return subjectName.compareTo(otherSubject.subjectName);
    }
}
