package nl.miwgroningen.c11.cerberus.docentenportaalproject.controller;

import lombok.RequiredArgsConstructor;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Cohort;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Role;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Student;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.User;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.CohortRepository;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.RoleRepository;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.StudentRepository;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/student")
public class StudentController {
    private final CohortRepository cohortRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @GetMapping("/all")
    private String showStudentOverview(Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Set<String> userRoles = user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toSet());
        List<Student> allStudents = new ArrayList<>();

        if(userRoles.contains("ADMIN")) {
            allStudents = studentRepository.findAll();
        }
        else if(userRoles.contains("TEACHER")) {
            List<Cohort> cohorts = cohortRepository.findCohortsByTeacherUsername(user.getUsername());
            for (Cohort cohort : cohorts) {
                allStudents.addAll(cohort.getStudents());
            }
        }
        else if(userRoles.contains("STUDENT")) {
            allStudents = getStudentsInCohortOfUser(user);
        }

        Collections.sort(allStudents);
        model.addAttribute("allStudents", allStudents);

        return "student/studentOverview";
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
    private String saveOrUpdateStudent(@ModelAttribute("student") Student studentToBeSaved, BindingResult result, Model model) {

        if (!result.hasErrors()) {
            if (studentToBeSaved.getUserId() == null) {
                studentToBeSaved.generateUsernameAndPassword(userRepository);
                String tempPassword = studentToBeSaved.getPassword();
                studentToBeSaved.hashPassword();

                addStudentRole(studentToBeSaved);

                studentToBeSaved.setFirstLogin(true);
                studentRepository.save(studentToBeSaved);
                model.addAttribute("username", studentToBeSaved.getUsername());
                model.addAttribute("password", tempPassword);

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

    private void addStudentRole(Student student) {
        Optional<Role> studentRole = roleRepository.findRoleByRoleName("STUDENT");
        if(studentRole.isPresent()){
            Set<Role> studentRoles = new HashSet<>();
            studentRoles.add(studentRole.get());
            student.setRoles(studentRoles);
        }
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
