package nl.miwgroningen.c11.cerberus.docentenportaalproject.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A field of study, consisting of a number of subjects
 *
 * @author Marianne Kooistra, Mohammed Almameri, Joost Schreuder
 */

@Entity
@Builder
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Programme {

    @Id
    @GeneratedValue
    private Long programmeId;

    private String programmeName;

    @Lob
    private String shortDescription;

    @ManyToMany
    private Set<Subject> subjects = new HashSet<>();

    @OneToMany(mappedBy="programme")
    private List<Cohort> cohorts = new ArrayList<>();

    @OneToOne
    private Image image;
}
