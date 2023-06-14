package nl.miwgroningen.c11.cerberus.docentenportaalproject.controller;

import lombok.RequiredArgsConstructor;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Programme;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.ProgrammeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ProgrammeController {
    private final ProgrammeRepository programmeRepository;

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
