package nl.miwgroningen.c11.cerberus.docentenportaalproject.controller;

import lombok.RequiredArgsConstructor;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Cohort;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Student;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.CohortRepository;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.ProgrammeRepository;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.StudentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//TODO Validation of dates (startDate before endDate)

@Controller
@RequiredArgsConstructor
public class CohortController {

    private final CohortRepository cohortRepository;
    private final StudentRepository studentRepository;
    private final ProgrammeRepository programmeRepository;

    @GetMapping("/cohort/all")
    private String showAllCohorts(Model model) {

        model.addAttribute("allCohorts", cohortRepository.findAll());

        return "/cohort/cohortOverview";
    }

    @GetMapping("/cohort/{cohortId}")
    private String getCohortDetails(@PathVariable("cohortId") Long cohortId, Model model) {
        Cohort cohort = getCohortDetails(cohortId);
        if (cohort == null) return "redirect:/cohort/all";

        List<Student> allStudents = getListOfStudentsWithoutOrInCurrentCohort(cohort);
        model.addAttribute("allStudents", allStudents);

        model.addAttribute("cohort", cohort);

        return "/cohort/cohortDetails";
    }

    @GetMapping("/cohort/new")
    private String showCreateCohortForm(Model model) {
        //New cohort, but with both dates set to avoid a NullPointerException
        Cohort newCohort = new Cohort();
        newCohort.setStartDate(LocalDate.now());
        newCohort.setEndDate(LocalDate.now());
        model.addAttribute("cohort", newCohort);

        List<Student> allStudents = getListOfStudentsWithoutCohort();

        model.addAttribute("allStudents", allStudents);
        model.addAttribute("allProgrammes", programmeRepository.findAll());

        return "/cohort/createCohortForm";
    }

    @GetMapping("/cohort/edit/{cohortId}")
    private String showEditCohortForm(@PathVariable("cohortId") Long cohortId, Model model) {
        Cohort cohort = getCohortDetails(cohortId);
        if (cohort == null) return "redirect:/cohort/all";

        //In the edit form, show a list of students that either have no cohort or are in the current cohort
        List<Student> allStudents = getListOfStudentsWithoutOrInCurrentCohort(cohort);

        model.addAttribute("cohort", cohort);
        model.addAttribute("allStudents", allStudents);
        model.addAttribute("allProgrammes", programmeRepository.findAll());

        return "/cohort/createCohortForm";
    }

    private Cohort getCohortDetails(Long cohortId) {
        Optional<Cohort> optionalCohort = cohortRepository.findById(cohortId);

        return optionalCohort.orElse(null);
    }

    private List<Student> getListOfStudentsWithoutCohort() {
        List<Student> allStudents = studentRepository.findAll();
        List<Student> studentsToBeRemoved = new ArrayList<>();

        //Done this way to not edit list during iteration
        for (Student student : allStudents) {

            if(student.getCohort() != null) {
                studentsToBeRemoved.add(student);
            }

        }

        allStudents.removeAll(studentsToBeRemoved);

        return allStudents;
    }

    //Gets a list of students that either (1) do not have a cohort (2) or are in the cohort that is passed on
    private List<Student> getListOfStudentsWithoutOrInCurrentCohort(Cohort cohort) {
        List<Student> allStudents = getListOfStudentsWithoutCohort();
        List<Student> studentsCurrentCohort = cohort.getStudents();
        allStudents.addAll(studentsCurrentCohort);
        return allStudents;
    }

    @PostMapping("/cohort/new")
    private String saveOrUpdateCohort(@ModelAttribute("cohort") Cohort cohortToBeSaved, BindingResult result) {

        if(!result.hasErrors()) {
            removeAllStudentsFromOldCohort(cohortToBeSaved);

            //Save cohort
            cohortRepository.save(cohortToBeSaved);

            List<Student> students = cohortToBeSaved.getStudents();

            //Set all selected students to that cohort
            for (Student student : students) {
                student.setCohort(cohortToBeSaved);
                studentRepository.save(student);
            }
        }

        Long cohortId = cohortToBeSaved.getCohortId();

        return "redirect:/cohort/" + cohortId;
    }

    //Finds if there is a cohort with the same id, sets empty the cohort of all students in there
    private void removeAllStudentsFromOldCohort(Cohort cohortToBeSaved) {
        //See if there is a cohort with the same id
        if(cohortToBeSaved.getCohortId() != null) {
            Optional<Cohort> optionalOldCohort = cohortRepository.findById(cohortToBeSaved.getCohortId());

            //If so, set the cohort of all students in that cohort to null
            if(optionalOldCohort.isPresent()) {
                Cohort cohort = optionalOldCohort.get();

                for (Student student : cohort.getStudents()) {
                    student.setCohort(null);
                }
            }
        }
    }

    @GetMapping("cohort/delete/{cohortId}")
    private String deleteCohort(@PathVariable("cohortId") Long cohortId) {
        Optional<Cohort> optionalCohort = cohortRepository.findById(cohortId);

        if(optionalCohort.isPresent()) {
            Cohort cohort = optionalCohort.get();

            //First, remove the cohort in question from all students
            for (Student student : cohort.getStudents()) {
                student.setCohort(null);
                studentRepository.save(student);
            }

            //Then, remove cohort
            cohortRepository.delete(optionalCohort.get());
        }

        return "redirect:/cohort/all";
    }
}
