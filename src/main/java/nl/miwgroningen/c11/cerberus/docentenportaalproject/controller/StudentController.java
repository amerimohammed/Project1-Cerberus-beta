package nl.miwgroningen.c11.cerberus.docentenportaalproject.controller;

import lombok.RequiredArgsConstructor;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Cohort;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Role;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Student;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.User;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.CohortRepository;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.StudentRepository;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles all actions concerning students.
 *
 * @author Marianne Kooistra, Mohammed Almameri, Joost Schreuder
 */

@Controller
@RequiredArgsConstructor
@RequestMapping("/student")
public class StudentController {
    private final CohortRepository cohortRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final UserController userController;

    @GetMapping("/all")
    private String showStudentOverview(Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Student> allStudents = getStudentListForUser(user);

        Collections.sort(allStudents);
        model.addAttribute("allStudents", allStudents);

        return "student/studentOverview";
    }

    private List<Student> getStudentListForUser(User user) {
        Set<String> userRoles = user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toSet());
        List<Student> allStudents = new ArrayList<>();

        if(userRoles.contains("ADMIN")) {
            allStudents = studentRepository.findAll();
        }
        else if(userRoles.contains("TEACHER")) {
            List<Cohort> cohorts = cohortRepository.findCohortsByTeacherUsername(user.getUsername());
            allStudents = getAllStudentsInCohorts(cohorts);
        }
        else if(userRoles.contains("STUDENT")) {
            allStudents = getStudentsInCohortOfUser(user);
        }
        return allStudents;
    }

    private List<Student> getAllStudentsInCohorts(List<Cohort> cohorts) {
        List<Student> allStudents = new ArrayList<>();

        for (Cohort cohort : cohorts) {
            allStudents.addAll(cohort.getStudents());
        }

        return allStudents;
    }

    private List<Student> getStudentsInCohortOfUser(User user) {
        List<Student> students = new ArrayList<>();

        Optional<Student> optionalStudent = studentRepository.findById(user.getUserId());
        if(optionalStudent.isPresent()) {
            Cohort studentCohort = optionalStudent.get().getCohort();
            students = studentCohort.getStudents();
        }

        return students;
    }

    @GetMapping("/new")
    private String showCreateStudentForm(Model model) {
        model.addAttribute("allCohorts", cohortRepository.findAll());
        model.addAttribute("student", new Student());

        return "student/createStudentForm";
    }

    @GetMapping("/edit/{studentId}")
    private String showEditStudentForm(@PathVariable("studentId") Long studentId, Model model) {
        Optional<Student> optionalStudent = studentRepository.findById(studentId);

        if (optionalStudent.isPresent()) {
            model.addAttribute("student", optionalStudent.get());
            model.addAttribute("allCohorts", cohortRepository.findAll());
            return "student/createStudentForm";
        }

        return "redirect:/student/all";
    }

    @PostMapping("/new")
    private String saveOrUpdateStudent(@ModelAttribute("student") Student studentToBeSaved,
                                       BindingResult result, Model model) {

        if (!result.hasErrors()) {
            if (studentToBeSaved.getUserId() == null) {
                userController.createUser(studentToBeSaved, model);

                return "student/studentAccount";
            } else {
                Optional<Student> storedStudent = studentRepository.findById(studentToBeSaved.getUserId());
                if (storedStudent.isPresent()) {
                    studentRepository.save(studentToBeSaved);
                }
            }
        }

        return "redirect:/student/all";
    }

    @PostMapping(value = "/new", params = "cancel")
    private String cancelForm() {

        return "redirect:/student/all";
    }

    @GetMapping("/delete/{studentId}")
    private String deleteStudent(@PathVariable("studentId") Long studentId) {
        Optional<Student> optionalStudent = studentRepository.findById(studentId);
        Optional<User> optionalUser = userRepository.findById(studentId);

        if (optionalStudent.isPresent() && optionalUser.isPresent()) {
            userRepository.delete(optionalUser.get());
            studentRepository.delete(optionalStudent.get());
        }

        return "redirect:/student/all";
    }

}
