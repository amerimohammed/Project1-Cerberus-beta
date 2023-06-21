package nl.miwgroningen.c11.cerberus.docentenportaalproject.controller;

import lombok.RequiredArgsConstructor;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Assignment;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.AssignmentRepository;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.SubjectRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/assignment")
public class AssignmentController {
    private final AssignmentRepository assignmentRepository;
    private final SubjectRepository subjectRepository;

    @GetMapping("/all")
    private String showAssignmentOverview(Model model) {
        model.addAttribute("allAssignments", assignmentRepository.findAll());

        return "assignment/assignmentOverview";
    }

    @GetMapping("/new")
    private String showCreateAssignmentForm(Model model) {
        model.addAttribute("assignment", new Assignment());
        model.addAttribute("allSubjects", subjectRepository.findAll());

        return "assignment/createAssignmentForm";
    }

    @GetMapping("/edit/{assignmentId}")
    private String showEditAssignmentForm(@PathVariable("assignmentId") Long assignmentId, Model model) {
        Optional<Assignment> optionalAssignment = assignmentRepository.findById(assignmentId);

        if (optionalAssignment.isPresent()) {
            model.addAttribute("assignment", optionalAssignment.get());
            model.addAttribute("allSubjects", subjectRepository.findAll());
            return "assignment/createAssignmentForm";
        }

        return "redirect:/assignment/all";
    }

    @GetMapping("/delete/{assignmentId}")
    private String deleteAssignment(@PathVariable("assignmentId") Long assignmentId) {
        Optional<Assignment> optionalAssignment = assignmentRepository.findById(assignmentId);

        if (optionalAssignment.isPresent()) {
            assignmentRepository.delete(optionalAssignment.get());
        }

        return "redirect:/assignment/all";
    }

    @PostMapping("/new")
    private String saveOrUpdateAssignment(@ModelAttribute("assignment") Assignment assignmentToBeSaved, BindingResult result) {

        if (!result.hasErrors()) {
            assignmentRepository.save(assignmentToBeSaved);
        }

        return "redirect:/assignment/all";
    }

    @PostMapping(value = "/new", params = "cancel")
    private String cancelForm() {

        return "redirect:/assignment/all";
    }
}
