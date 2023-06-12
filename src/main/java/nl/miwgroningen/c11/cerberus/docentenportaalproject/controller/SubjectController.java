package nl.miwgroningen.c11.cerberus.docentenportaalproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SubjectController {

    @GetMapping({"/","/subject/all"})
    private String showSubjectOverview() {
        return "SubjectOverview";
    }
}
