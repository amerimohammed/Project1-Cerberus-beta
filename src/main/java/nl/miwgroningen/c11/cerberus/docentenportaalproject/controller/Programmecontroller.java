package nl.miwgroningen.c11.cerberus.docentenportaalproject.controller;

import lombok.RequiredArgsConstructor;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Programme;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.ProgrammeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

/**
 * Handels all actions concerning programme.
 * Gemaakt door Marianne Kooistra (me.kooistra@st.hanze.nl) op 14/06/2023
 */

@Controller
@RequiredArgsConstructor
public class Programmecontroller {
    private final ProgrammeRepository programmeRepository;

    @GetMapping("/home")
    // TODO: "/home" needs to be just "/" but but subjects/all still has that.
    private String showHomePage(Model model) {
        model.addAttribute("allProgrammes", programmeRepository.findAll());

        return "HomePage";
    }

    // TODO: new and edit?

    @GetMapping("/home/delete/{programmeId}")
    private String deleteProgramme(@PathVariable("programmeId") Long programmeId) {
        Optional<Programme> optionalProgramme = programmeRepository.findById(programmeId);

        if (optionalProgramme.isPresent()) {
            programmeRepository.delete(optionalProgramme.get());
        }
        
        return "redirect:/home";
    }

}
