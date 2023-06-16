package nl.miwgroningen.c11.cerberus.docentenportaalproject.controller;

import lombok.RequiredArgsConstructor;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Student;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.User;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.CohortRepository;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.StudentRepository;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {
    private final CohortRepository cohortRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    @GetMapping("/all")
    private String showStudentOverview(Model model) {
        model.addAttribute("allStudents", studentRepository.findAll());

        return "Student/studentOverview";
    }

    @GetMapping("/new")
    private String showCreateStudentForm(Model model) {
        model.addAttribute("allCohorts", cohortRepository.findAll());
        model.addAttribute("student", new Student());

        return "Student/createStudentForm";
    }

    @GetMapping("/edit/{studentId}")
    private String showEditStudentForm(@PathVariable("studentId") Long studentId, Model model) {
        Optional<Student> optionalStudent = studentRepository.findById(studentId);

        if (optionalStudent.isPresent()) {
            model.addAttribute("student", optionalStudent.get());
            model.addAttribute("allCohorts", cohortRepository.findAll());
            return "Student/createStudentForm";
        }

        return "redirect:/student/all";
    }

    @PostMapping("/new")
    private String saveOrUpdateStudent(@ModelAttribute("student") Student studentToBeSaved, BindingResult result, Model model) {

        if (!result.hasErrors()) {
            if (studentToBeSaved.getUserId() == null) {
                studentToBeSaved.generateUsernameAndPassword();
                String tempPassword = studentToBeSaved.getPassword();
                studentToBeSaved.hashPassword();
                studentRepository.save(studentToBeSaved);
                model.addAttribute("username", studentToBeSaved.getUsername());
                model.addAttribute("password", tempPassword);
                return "Student/studentAccount";
            } else {
                Optional<Student> storedStudent = studentRepository.findById(studentToBeSaved.getUserId());
                if (storedStudent.isPresent()) {
                    studentToBeSaved.setUsername(storedStudent.get().getUsername());
                    studentToBeSaved.setPassword(storedStudent.get().getPassword());
                    studentRepository.save(studentToBeSaved);
                }
            }
        }

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
