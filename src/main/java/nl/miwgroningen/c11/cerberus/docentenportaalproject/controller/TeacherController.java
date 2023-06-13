package nl.miwgroningen.c11.cerberus.docentenportaalproject.controller;

import lombok.RequiredArgsConstructor;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Student;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Teacher;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.TeacherRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * /**
 * handles all pages of the teacher
 *
 * @author Marianne Kooistra, Mohammed Almameri, Joost Schreuder
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/teacher")
public class TeacherController {

    private final TeacherRepository teacherRepository;

    @GetMapping({"", "/all"})
    private String showTeacherOverview(Model model) {
        model.addAttribute("allTeachers", teacherRepository.findAll());

        return "teacher/teacherOverview";
    }

    @GetMapping("/new")
    private String showCreateTeacherForm(Model model) {
        model.addAttribute("teacher", new Teacher());

        return "teacher/createTeacherForm";
    }

    @GetMapping("/edit/{teacherId}")
    private String showEditTeacherForm(@PathVariable("teacherId") Long teacherId, Model model) {
        Optional<Teacher> optionalTeacher = teacherRepository.findById(teacherId);

        if (optionalTeacher.isPresent()) {
            model.addAttribute("teacher", optionalTeacher.get());
            return "teacher/createTeacherForm";
        }

        return "redirect:/teacher/all";
    }

    @PostMapping("")
    private String saveOrUpdateTeacher(@ModelAttribute("teacher") Teacher teacherToBeSaved, BindingResult result) {

        if (!result.hasErrors()) {
            teacherRepository.save(teacherToBeSaved);
        }

        return "redirect:/teacher/all";
    }

    @GetMapping("/teacher/delete/{teacherId}")
    private String deleteTeacher(@PathVariable("teacherId") Long teacherId) {
        Optional<Teacher> optionalTeacher = teacherRepository.findById(teacherId);

        if (optionalTeacher.isPresent()) {
            teacherRepository.delete(optionalTeacher.get());
        }

        return "redirect:/teacher/all";
    }
}
