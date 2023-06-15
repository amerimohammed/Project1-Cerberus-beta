package nl.miwgroningen.c11.cerberus.docentenportaalproject.controller;

import lombok.RequiredArgsConstructor;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Subject;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.SubjectRepository;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.TeacherRepository;
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
public class SubjectController {
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;

    @GetMapping("/subject/all")
    private String showSubjectOverview(Model model) {
        model.addAttribute("allSubjects", subjectRepository.findAll());

        return "Subject/subjectOverview";
    }

    @GetMapping("/subject/new")
    private String showCreateSubjectForm(Model model) {
        model.addAttribute("allTeachers", teacherRepository.findAll());
        model.addAttribute("subject", new Subject());

        return "Subject/createSubjectForm";
    }

    @GetMapping("subject/edit/{subjectId}")
    private String showEditSubjectForm(@PathVariable("subjectId") Long subjectId, Model model) {
        Optional<Subject> optionalSubject = subjectRepository.findById(subjectId);

        if (optionalSubject.isPresent()) {
            model.addAttribute("subject", optionalSubject.get());
            model.addAttribute("allTeachers", teacherRepository.findAll());

            return "Subject/createSubjectForm";
        }

        return "redirect:/subject/all";
    }

    @PostMapping("/subject/new")
    private String saveOrUpdateStudent(@ModelAttribute("subject") Subject subjectToBeSaved, BindingResult result) {

        if (!result.hasErrors()) {
            subjectRepository.save(subjectToBeSaved);
        }

        return "redirect:/subject/all";
    }

    @GetMapping("subject/delete/{subjectId}")
    private String deleteSubject(@PathVariable("subjectId") Long subjectId) {
        Optional<Subject> optionalSubject = subjectRepository.findById(subjectId);

        if (optionalSubject.isPresent()) {
            subjectRepository.delete(optionalSubject.get());
        }

        return "redirect:/subject/all";
    }
}
