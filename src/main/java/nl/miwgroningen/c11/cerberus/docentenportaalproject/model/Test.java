package nl.miwgroningen.c11.cerberus.docentenportaalproject.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * A type of assignment that occurs at a set date and serves as an evaluation moment,
 * to evaluate the learned skills of a student in a subject.
 *
 * @author Marianne Kooistra, Mohammed Almameri, Joost Schreuder
 */

@Entity
@SuperBuilder
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Test extends Assignment implements Comparable<Test> {

    private String testName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate testDate;

    @ManyToOne
    private Test superTest;

    @OneToMany(mappedBy = "superTest", cascade = CascadeType.ALL)
    private List<Test> testParts;

    //test contents in this context means the explanation and questions that make up the test.
    @Lob
    private String testContents;

    @OneToMany(mappedBy = "test")
    private List<TestAttempt> testAttempts;

    public String displayTestDate() {
        String display;
        if (testDate == null) {
            display = "No date";
        } else {
            display = String.format("%02d - %02d - %4d", testDate.getDayOfMonth(), testDate.getMonthValue(), testDate.getYear());
        }

        return display;
    }

    public String displayQuestionNumber() {
        String questionNumber = "";

        if (superTest != null) {
            if (!superTest.displayQuestionNumber().equals("")) {
                questionNumber = superTest.displayQuestionNumber() + ".";
            }

            int index = 0;

            while (superTest.testParts.get(index) != this) {
                index++;
            }

            questionNumber = questionNumber + (index + 1);
        }

        return questionNumber;
    }

    public void inheritFromSuper(Test superTest) {
        for (Test testPart : superTest.testParts) {
            //Inherit date
            if (testPart.testDate == null && superTest.testDate != null) {
                testPart.setTestDate(superTest.testDate);
            }

            //InheritSubject
            if (testPart.getSubject() == null) {
                testPart.setSubject(superTest.getSubject());
            }

            //Do the same for all testParts of testParts
            if (testPart.testParts != null) {
                inheritFromSuper(testPart);
            }
        }
    }

    //Gets all the students that are eligible for a test
    //by taking a test -> subject -> programmes -> cohorts -> students route
    public List<Student> getAllStudentsTakingTest() {
        List<Programme> testProgrammes = getSubject().getProgrammes();
        List<Cohort> testCohorts = new ArrayList<>();

        for (Programme testProgramme : testProgrammes) {
            testCohorts.addAll(testProgramme.getCohorts());
        }

        List<Student> allStudentsTakingTest = new ArrayList<>();
        for (Cohort testCohort : testCohorts) {
            allStudentsTakingTest.addAll(testCohort.getStudents());
        }

        return allStudentsTakingTest;
    }

    @Override
    public int compareTo(Test otherTest) {
        return otherTest.testDate.compareTo(testDate);
    }
}
