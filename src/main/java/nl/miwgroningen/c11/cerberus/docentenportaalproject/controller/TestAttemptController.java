package nl.miwgroningen.c11.cerberus.docentenportaalproject.controller;

import lombok.RequiredArgsConstructor;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.*;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.StudentRepository;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.TestAttemptRepository;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.TestRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class TestAttemptController {

    private final TestRepository testRepository;
    private final StudentRepository studentRepository;
    private final TestAttemptRepository testAttemptRepository;

    @GetMapping("/test/{testId}/attempt/all")
    private String showTestAttemptOverview(@PathVariable("testId") Long testId, Model model) {
        Optional<Test> optionalTest = testRepository.findById(testId);

        if(optionalTest.isEmpty()) {
            return("redirect:/test/all");
        }

        Test test = optionalTest.get();
        List<Student> allStudents = getAllStudentsTakingTest(test);

        model.addAttribute("test", test);
        model.addAttribute("allStudents", allStudents);

        return("testAttempt/testAttemptOverview");
    }

    //Gets all the students that are eligible for a test
    //by taking a test -> subject -> programmes -> cohorts -> students route
    private List<Student> getAllStudentsTakingTest(Test test) {
        List<Programme> testProgrammes = test.getSubject().getProgrammes();
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

    @GetMapping("/test/{testId}/attempt/{testAttemptId}")
    private String showTestAttemptDetails(@PathVariable("testAttemptId") Long testAttemptId, Model model) {
        Optional<TestAttempt> optionalTestAttempt = testAttemptRepository.findById(testAttemptId);

        if(optionalTestAttempt.isEmpty()) {
            return("redirect:/test/all");
        }

        TestAttempt testAttempt = optionalTestAttempt.get();

        model.addAttribute("testAttempt", testAttempt);

        return("testAttempt/testAttemptDetails");
    }

    @GetMapping("/test/{testId}/attempt/{studentId}/add")
    private String createTestAttempt(@PathVariable("testId") Long testId, @PathVariable("studentId") Long studentId) {
        Optional<Test> optionalTest = testRepository.findById(testId);
        Optional<Student> optionalStudent = studentRepository.findById(studentId);

        if(optionalTest.isPresent() && optionalStudent.isPresent()) {
            TestAttempt testAttempt = new TestAttempt(optionalTest.get(), optionalStudent.get());

            createRecursiveTestAttempts(testAttempt, null);

            return "redirect:/test/{testId}/attempt/" + testAttempt.getTestAttemptId();
        }

        return("redirect:/test/all");
    }

//    Create a testAttempt for each part of the test
    private void createRecursiveTestAttempts(TestAttempt testAttempt, TestAttempt superTestAttempt) {

        if(superTestAttempt != null) {
            testAttempt.setSuperTestAttempt(superTestAttempt);
        }

        List<Test> subTests = testAttempt.getTest().getTestParts();

        //If there are any subtests, make a testAttempt for each of them by using the same method
        if(subTests != null) {
            List<TestAttempt> subTestAttempts = new ArrayList<>();

            for (Test subTest : subTests) {
                TestAttempt subTestAttempt = new TestAttempt(subTest, testAttempt.getStudent());
                subTestAttempts.add(subTestAttempt);
                createRecursiveTestAttempts(subTestAttempt, testAttempt);
            }

            testAttempt.setSubTestAttempts(subTestAttempts);
        }

        testAttemptRepository.save(testAttempt);
    }
}
