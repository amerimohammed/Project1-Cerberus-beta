package nl.miwgroningen.c11.cerberus.docentenportaalproject.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
    private List<Subject> subjects = new ArrayList<>();

    @OneToMany(mappedBy="programme")
    private List<Cohort> cohorts = new ArrayList<>();

    @OneToOne
    private Image image;
}
