package nl.miwgroningen.c11.cerberus.docentenportaalproject.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * An attempt of a student on (part of a) test, including a grade and feedback
 *
 * @author Marianne Kooistra, Mohammed Almameri, Joost Schreuder
 */

@Entity
@Getter @Setter
@NoArgsConstructor
public class TestAttempt {

    @Id
    @GeneratedValue
    private long testAttemptId;

    @ManyToOne
    private Test test;

    @ManyToOne
    private Student student;

    @OneToMany(mappedBy="superTestAttempt", cascade = CascadeType.ALL)
    private List<TestAttempt> subTestAttempts;

    @ManyToOne
    private TestAttempt superTestAttempt;

    private int score;
    private String feedback;

    public TestAttempt(Test test, Student student) {
        this.test = test;
        this.student = student;
    }

    public int getDepth() {
        if (superTestAttempt == null) {
            return 0;
        }
        return 1 + superTestAttempt.getDepth();
    }

    //Lowest level -> set score to value
    //Then, it will call updateScoreRecursively() to sum up everything for each level until the top level
    //So, scores can only be set for the lowest level
    public void setScore(int score) {
        if(subTestAttempts != null) {
            return;
        }

        this.score = score;

        if(superTestAttempt != null) {
            updateScoresRecursively(superTestAttempt);
        }

    }

    public void updateScoresRecursively(TestAttempt testAttempt) {
        int sumScore = 0;

        for (TestAttempt subTestAttempt : testAttempt.subTestAttempts) {
            sumScore += subTestAttempt.score;
        }

        testAttempt.score = sumScore;

        if(testAttempt.superTestAttempt != null) {
            updateScoresRecursively(testAttempt.superTestAttempt);
        }
    }
}
