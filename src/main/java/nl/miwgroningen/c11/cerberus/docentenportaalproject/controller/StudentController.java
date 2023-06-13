package nl.miwgroningen.c11.cerberus.docentenportaalproject.controller;

import lombok.RequiredArgsConstructor;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Student;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.StudentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class StudentController {
    private final StudentRepository studentRepository;

    @GetMapping("/student/all")
    private String showStudentOverview(Model model) {
        model.addAttribute("allStudents", studentRepository.findAll());

        return "Student/studentOverview";
    }

    @GetMapping("/student/new")
    private String showCreateStudentForm(Model model) {
        model.addAttribute("student", new Student());

        return "Student/createStudentForm";
    }

    @GetMapping("/student/edit/{studentId}")
    private String showEditStudentForm(@PathVariable("studentId") Long studentId, Model model) {
        Optional<Student> optionalStudent = studentRepository.findById(studentId);

        if (optionalStudent.isPresent()) {
            model.addAttribute("student", optionalStudent.get());
            return "Student/createStudentForm";
        }

        return "redirect:/student/all";
    }

    @PostMapping("/student/new")
    private String saveOrUpdateStudent(@ModelAttribute("student") Student studentToBeSaved, BindingResult result) {

        if (!result.hasErrors()) {
            studentRepository.save(studentToBeSaved);
        }

        return "redirect:/student/all";
    }

}