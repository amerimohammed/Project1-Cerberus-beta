package nl.miwgroningen.c11.cerberus.docentenportaalproject.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * A person teaching a subject.
 *
 * @author Marianne Kooistra, Mohammed Almameri, Joost Schreuder
 */

@Entity
@Getter @Setter
public class Teacher {
    @Id
    @GeneratedValue
    private Long teacherId;

    private String name;

    @ManyToMany
    private List<Subject> subjects = new ArrayList<>();
}
