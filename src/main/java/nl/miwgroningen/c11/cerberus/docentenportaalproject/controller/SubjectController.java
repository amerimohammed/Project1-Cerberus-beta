package nl.miwgroningen.c11.cerberus.docentenportaalproject.controller;

import lombok.RequiredArgsConstructor;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Subject;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.SubjectRepository;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.TeacherRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/subject")
public class SubjectController {
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;

    @GetMapping("/all")
    private String showSubjectOverview(Model model) {
        model.addAttribute("allSubjects", subjectRepository.findAll());

        return "subject/subjectOverview";
    }

    @GetMapping("/new")
    private String showCreateSubjectForm(Model model) {
        model.addAttribute("allTeachers", teacherRepository.findAll());
        model.addAttribute("subject", new Subject());

        return "subject/createSubjectForm";
    }

    @GetMapping("/edit/{subjectId}")
    private String showEditSubjectForm(@PathVariable("subjectId") Long subjectId, Model model) {
        Optional<Subject> optionalSubject = subjectRepository.findById(subjectId);

        if (optionalSubject.isPresent()) {
            model.addAttribute("subject", optionalSubject.get());
            model.addAttribute("allTeachers", teacherRepository.findAll());

            return "subject/createSubjectForm";
        }

        return "redirect:/subject/all";
    }

    @PostMapping("/new")
    private String saveOrUpdateStudent(@ModelAttribute("subject") Subject subjectToBeSaved, BindingResult result) {

        if (!result.hasErrors()) {
            subjectRepository.save(subjectToBeSaved);
        }

        return "redirect:/subject/all";
    }

    @PostMapping(value = "/new", params = "cancel")
    private String cancelForm(@ModelAttribute("subject") Subject subjectToCancelSave) {

        return "redirect:/subject/all";
    }

    @GetMapping("/delete/{subjectId}")
    private String deleteSubject(@PathVariable("subjectId") Long subjectId, Model model) {
        Optional<Subject> optionalSubject = subjectRepository.findById(subjectId);

        if (optionalSubject.isPresent()) {
            try {
                subjectRepository.delete(optionalSubject.get());
            } catch (DataIntegrityViolationException dataIntegrityViolationException) {
                System.out.println(dataIntegrityViolationException.getMessage());
                model.addAttribute("errorMessage",
                        "This Subject can't be deleted due to relation to other entities");
                return "error";
            }
        }

        return "redirect:/subject/all";
    }
}
