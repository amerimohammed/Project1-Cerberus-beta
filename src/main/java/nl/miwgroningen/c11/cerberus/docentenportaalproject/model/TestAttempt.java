package nl.miwgroningen.c11.cerberus.docentenportaalproject.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

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

    private String answer;

    private int score = -1;
    private String feedback;

    public TestAttempt(Test test, Student student) {
        this.test = test;
        this.student = student;
    }

    public long getSuperTestId() {
        TestAttempt testAttempt = this;

        while(testAttempt.superTestAttempt != null) {
            testAttempt = testAttempt.superTestAttempt;
        }

        return testAttempt.testAttemptId;
    }

    public boolean hasSubTestAttempts() {
        return subTestAttempts.size() > 0;
    }

    //Displays a warning if a part of a test has not been graded yet
    public String displayScore() {
        if(score == -1) {
            return "TO BE GRADED";
        }
        else return Integer.toString(score);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestAttempt that = (TestAttempt) o;
        return testAttemptId == that.testAttemptId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(testAttemptId);
    }
}
