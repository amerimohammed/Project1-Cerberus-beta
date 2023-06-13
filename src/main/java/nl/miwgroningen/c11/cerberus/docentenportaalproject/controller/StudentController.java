package nl.miwgroningen.c11.cerberus.docentenportaalproject.controller;

import lombok.RequiredArgsConstructor;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Student;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.CohortRepository;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.StudentRepository;
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
    private String saveOrUpdateStudent(@ModelAttribute("student") Student studentToBeSaved, BindingResult result) {

        if (!result.hasErrors()) {
            studentRepository.save(studentToBeSaved);
        }

        return "redirect:/student/all";
    }

    @GetMapping("/delete/{studentId}")
    private String deleteStudent(@PathVariable("studentId") Long studentId) {
        Optional<Student> optionalStudent = studentRepository.findById(studentId);

        if (optionalStudent.isPresent()) {
            studentRepository.delete(optionalStudent.get());
        }

        return "redirect:/student/all";
    }

}
