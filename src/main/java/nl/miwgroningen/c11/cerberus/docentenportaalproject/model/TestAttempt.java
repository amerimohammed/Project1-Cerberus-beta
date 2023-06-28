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
@Getter
@Setter
@NoArgsConstructor
public class TestAttempt {

    @Id
    @GeneratedValue
    private long testAttemptId;

    @ManyToOne
    private Test test;

    @ManyToOne
    private Student student;

    @OneToMany(mappedBy = "superTestAttempt", cascade = CascadeType.ALL)
    private List<TestAttempt> subTestAttempts;

    @ManyToOne
    private TestAttempt superTestAttempt;

    private int score;
    private boolean isGraded = false;

    @Lob
    private String answer;

    @Lob
    private String feedback;

    public TestAttempt(Test test, Student student) {
        this.test = test;
        this.student = student;
    }

    public long getSuperTestId() {
        TestAttempt testAttempt = this;

        while (testAttempt.superTestAttempt != null) {
            testAttempt = testAttempt.superTestAttempt;
        }

        return testAttempt.testAttemptId;
    }

    public boolean hasSubTestAttempts() {
        return subTestAttempts.size() > 0;
    }

    public int sumUpSubTestAttemptScores() {
        int sumScore = 0;

        for (TestAttempt subTestAttempt : subTestAttempts) {


            sumScore += subTestAttempt.score;
        }

        return sumScore;
    }

    //Displays a warning if a part of a test has not been graded yet
    public String displayScore() {
        if (!isGraded && !hasSubTestAttempts()) {
            return "TO BE GRADED";
        } else if (!isGraded) {
            return "";
        } else return Integer.toString(score);
    }

    @Override
    public boolean equals(Object comparisonObject) {
        if (this == comparisonObject) return true;
        if (comparisonObject == null || getClass() != comparisonObject.getClass()) return false;
        TestAttempt that = (TestAttempt) comparisonObject;
        return testAttemptId == that.testAttemptId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(testAttemptId);
    }

    //TODO: testen van deze methode
    public void setScore(int score) {
        if(score < 0) {
            throw new IllegalArgumentException("Score mag niet negatief zijn.");
        }
        else {
            this.score = score;
        }
    }

    public void setIsGraded(boolean isGraded) {
        if(!hasSubTestAttempts()) {
            this.isGraded = isGraded;
        }
        else {
            this.isGraded = checkAllSubTestsGraded();
        }
    }

    private boolean checkAllSubTestsGraded() {
        boolean allGraded = true;
        int index = 0;

        while(allGraded && subTestAttempts.size() > index) {
            if(!subTestAttempts.get(index).isGraded) {
                allGraded = false;
            }

            index++;
        }

        return allGraded;
    }
}
