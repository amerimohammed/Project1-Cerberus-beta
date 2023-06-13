package nl.miwgroningen.c11.cerberus.docentenportaalproject.controller;

import lombok.RequiredArgsConstructor;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Cohort;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Student;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.CohortRepository;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.StudentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CohortController {

    private final CohortRepository cohortRepository;
    private final StudentRepository studentRepository;

    @GetMapping("/cohort/all")
    private String showAllCohorts(Model model) {

        model.addAttribute("allCohorts", cohortRepository.findAll());

        return "/cohort/cohortOverview";
    }

    @GetMapping("/cohort/new")
    private String showCreateCohortForm(Model model) {
        model.addAttribute("cohort", new Cohort());
        model.addAttribute("allStudents", studentRepository.findAll());

        return "/cohort/createCohortForm";
    }

    @PostMapping("/cohort/new")
    private String saveOrUpdateCohort(@ModelAttribute("cohort") Cohort cohortToBeSaved, BindingResult result) {

        if(!result.hasErrors()) {
            //Save cohort first
            cohortRepository.save(cohortToBeSaved);

            List<Student> students = cohortToBeSaved.getStudents();

            //Then, set all students to that cohort
            for (Student student : students) {
                student.setCohort(cohortToBeSaved);
                studentRepository.save(student);
            }
        }

        return "redirect:/cohort/all";
    }
}
