package nl.miwgroningen.c11.cerberus.docentenportaalproject.controller;

import lombok.RequiredArgsConstructor;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Programme;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.ProgrammeRepository;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.SubjectRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

/**
 * Handels all actions concerning programme.
 * Gemaakt door Marianne Kooistra (me.kooistra@st.hanze.nl) op 14/06/2023
 */

@Controller
@RequiredArgsConstructor
public class ProgrammeController {
    private final ProgrammeRepository programmeRepository;
    private final SubjectRepository subjectRepository;

    @GetMapping({"/", "/home"})
    private String showHomePage(Model model) {
        model.addAttribute("allProgrammes", programmeRepository.findAll());

        return "HomePage";
    }

    @GetMapping("/programme/new")
    private String showCreateProgrammeForm(Model model) {
        model.addAttribute("allSubjects", subjectRepository.findAll());
        model.addAttribute("programme", new Programme());

        return "programme/createProgrammeForm";
    }

    @GetMapping("programme/edit/{programmeId}")
    private String showEditProgrammeForm(@PathVariable("programmeId") Long programmeId, Model model) {
        Optional<Programme> optionalProgramme = programmeRepository.findById(programmeId);

        if (optionalProgramme.isPresent()) {
            model.addAttribute("programme", optionalProgramme.get());
            model.addAttribute("allSubjects", subjectRepository.findAll());

            return "programme/createProgrammeForm";
        }

        return "redirect:/home";
    }

    @PostMapping("/programme/new")
    private String saveOrUpdateProgramme(@ModelAttribute("programme") Programme programmeToBeSaved, BindingResult result) {

        if (!result.hasErrors()) {
            programmeRepository.save(programmeToBeSaved);
        }

        return "redirect:/home";
    }

    @GetMapping("/home/delete/{programmeId}")
    private String deleteProgramme(@PathVariable("programmeId") Long programmeId) {
        Optional<Programme> optionalProgramme = programmeRepository.findById(programmeId);

        if (optionalProgramme.isPresent()) {
            programmeRepository.delete(optionalProgramme.get());
        }
        
        return "redirect:/home";
    }

    @GetMapping("/programme/view/{programmeId}")
    private String showProgrammeDetails(@PathVariable("programmeId") Long programmeId, Model model) {
        Optional<Programme> optionalProgramme = programmeRepository.findById(programmeId);
        if (optionalProgramme.isPresent()) {
            model.addAttribute("programme", optionalProgramme.get());
            return "programme/programmeDetails";
        }
        return "redirect:/programme/all";
    }

}
