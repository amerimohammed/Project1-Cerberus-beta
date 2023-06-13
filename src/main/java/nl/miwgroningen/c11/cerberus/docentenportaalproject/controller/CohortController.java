package nl.miwgroningen.c11.cerberus.docentenportaalproject.controller;

import lombok.RequiredArgsConstructor;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.CohortRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class CohortController {

    private final CohortRepository cohortRepository;

    @GetMapping("/cohort/all")
    private String showAllCohorts(Model model) {

        model.addAttribute("allCohorts", cohortRepository.findAll());

        return "/cohort/cohortOverview";
    }

}
