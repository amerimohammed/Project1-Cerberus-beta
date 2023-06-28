package nl.miwgroningen.c11.cerberus.docentenportaalproject.controller;

import lombok.RequiredArgsConstructor;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.*;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.StudentRepository;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.TestAttemptRepository;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.TestRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class TestAttemptController {

    private final TestRepository testRepository;
    private final StudentRepository studentRepository;
    private final TestAttemptRepository testAttemptRepository;

    @GetMapping("/testAttempt/all")
    private String showAllTestAttemptsOfStudentUser(Model model) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<Student> optionalStudent = studentRepository.findById(user.getUserId());

        if (optionalStudent.isEmpty()) {
            return "redirect:/test/all";
        }

        Student student = optionalStudent.get();

        List<Test> studentTestList = new ArrayList<>(student.getAllSuperTestAttempts());

        Collections.sort(studentTestList);

        model.addAttribute("allTest", studentTestList);
        model.addAttribute("student", student);

        return "testAttempt/testAttemptOverviewStudent";
    }

    @GetMapping("/test/{testId}/attempt/all")
    private String showStudentOverviewForTest(@PathVariable("testId") Long testId, Model model) {
        Optional<Test> optionalTest = testRepository.findById(testId);

        if (optionalTest.isEmpty()) {
            return ("redirect:/test/all");
        }

        Test test = optionalTest.get();
        List<Student> allStudents = test.getAllStudentsTakingTest();

        model.addAttribute("test", test);
        model.addAttribute("allStudents", allStudents);

        return ("testAttempt/testAttemptOverviewTeacher");
    }

    @GetMapping("/testAttempt/{testAttemptId}")
    private String showTestAttemptDetails(@PathVariable("testAttemptId") Long testAttemptId, Model model) {
        Optional<TestAttempt> optionalTestAttempt = testAttemptRepository.findById(testAttemptId);

        if (optionalTestAttempt.isEmpty()) {
            return ("redirect:/test/all");
        }

        TestAttempt testAttempt = optionalTestAttempt.get();

        model.addAttribute("testAttempt", testAttempt);
        model.addAttribute("subTestAttempts", testAttempt.getSubTestAttempts());

        return ("testAttempt/testAttemptDetails");
    }

    @GetMapping("/test/{testId}/attempt/{studentId}/add")
    private String createTestAttempt(@PathVariable("testId") Long testId, @PathVariable("studentId") Long studentId) {
        Optional<Test> optionalTest = testRepository.findById(testId);
        Optional<Student> optionalStudent = studentRepository.findById(studentId);

        if (optionalTest.isPresent() && optionalStudent.isPresent()) {
            TestAttempt testAttempt = new TestAttempt(optionalTest.get(), optionalStudent.get());

            createRecursiveTestAttempts(testAttempt, null);

            return "redirect:/testAttempt/" + testAttempt.getTestAttemptId();
        }

        return ("redirect:/test/all");
    }

    //Create a testAttempt for each part of the test
    private void createRecursiveTestAttempts(TestAttempt testAttempt, TestAttempt superTestAttempt) {

        if (superTestAttempt != null) {
            testAttempt.setSuperTestAttempt(superTestAttempt);
        }

        List<Test> subTests = testAttempt.getTest().getTestParts();

        //If there are any subtests, make a testAttempt for each of them by using the same method
        if (subTests != null) {
            List<TestAttempt> subTestAttempts = new ArrayList<>();

            for (Test subTest : subTests) {
                TestAttempt subTestAttempt = new TestAttempt(subTest, testAttempt.getStudent());
                subTestAttempts.add(subTestAttempt);
                testAttemptRepository.save(testAttempt);
                createRecursiveTestAttempts(subTestAttempt, testAttempt);
            }

            testAttempt.setSubTestAttempts(subTestAttempts);
        }

        testAttemptRepository.save(testAttempt);
    }

    @GetMapping("testAttempt/edit/student/{testAttemptId}")
    private String showTestAttemptStudentEditForm(@PathVariable("testAttemptId") Long testAttemptId, Model model) {
        Optional<TestAttempt> optionalTestAttempt = testAttemptRepository.findById(testAttemptId);

        if (optionalTestAttempt.isEmpty()) {
            return "redirect:/test/all";
        }

        model.addAttribute("testAttempt", optionalTestAttempt.get());

        return "/testAttempt/editTestAttemptStudent";
    }

    @GetMapping("testAttempt/edit/teacher/{testAttemptId}")
    private String showTestAttemptTeacherEditForm(@PathVariable("testAttemptId") Long testAttemptId, Model model) {
        Optional<TestAttempt> optionalTestAttempt = testAttemptRepository.findById(testAttemptId);

        if (optionalTestAttempt.isEmpty()) {
            return "redirect:/test/all";
        }

        model.addAttribute("testAttempt", optionalTestAttempt.get());

        return "/testAttempt/editTestAttemptTeacher";
    }

    //For hovers in testAttemptDetails - provides the right redirection dependent on role
    @GetMapping("testAttempt/edit/{testAttemptId}")
    private String redirectToTestAttemptEditForm(@PathVariable("testAttemptId") Long testAttemptId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Set<String> userRoles = user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toSet());

        if (userRoles.contains("ADMIN") || userRoles.contains("TEACHER")) {
            return "redirect:/testAttempt/edit/teacher/" + testAttemptId;
        } else if (userRoles.contains("STUDENT")) {
            return "redirect:/testAttempt/edit/student/" + testAttemptId;
        } else {
            return "redirect:/test/all";
        }
    }

    @PostMapping("/testAttempt/update")
    private String updateTestAttempt(@ModelAttribute("testAttempt") TestAttempt testAttemptToBeSaved,
                                     BindingResult result) {

        if (!result.hasErrors()) {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Set<String> userRoles = user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toSet());

            //If teacher saves, testAttempt is considered to be graded
            if(userRoles.contains("TEACHER")) {
                testAttemptToBeSaved.setIsGraded(true);
            }

            testAttemptRepository.save(testAttemptToBeSaved);

            if (testAttemptToBeSaved.getSuperTestAttempt() != null) {
                updateTestAttemptsRecursively(testAttemptToBeSaved.getSuperTestAttempt());
            }
        }

        return "redirect:/testAttempt/" + testAttemptToBeSaved.getSuperTestId();
    }

    //Updates the scores and grading status of higher levels of the test
    public void updateTestAttemptsRecursively(TestAttempt testAttempt) {
        int sumScore = testAttempt.sumUpSubTestAttemptScores();

        testAttempt.setScore(sumScore);
        testAttempt.setIsGraded(false);

        if (testAttempt.getSuperTestAttempt() != null) {
            updateTestAttemptsRecursively(testAttempt.getSuperTestAttempt());
        }

        testAttemptRepository.save(testAttempt);
    }

    @PostMapping(value = "/testAttempt/update", params = "cancel")
    private String cancelForm(@ModelAttribute("testAttempt") TestAttempt testAttemptToBeCanceled) {
        return "redirect:/testAttempt/" + testAttemptToBeCanceled.getSuperTestId();
    }
}
