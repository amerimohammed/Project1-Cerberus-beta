package nl.miwgroningen.c11.cerberus.docentenportaalproject.controller;

import lombok.RequiredArgsConstructor;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Cohort;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Role;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Student;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.User;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.CohortRepository;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.ProgrammeRepository;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.StudentRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cohort")
public class CohortController {
    private static final LocalDate DEFAULT_DATE = LocalDate.now();

    private final CohortRepository cohortRepository;
    private final StudentRepository studentRepository;
    private final ProgrammeRepository programmeRepository;

    @GetMapping("/all")
    private String showAllCohorts(Model model) {
        List<Cohort> allCohorts;

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Set<String> userRoles = user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toSet());

        if (!userRoles.contains("ADMIN")) {
            allCohorts = cohortRepository.findCohortsByTeacherUsername(user.getUsername());
        } else {
            allCohorts = cohortRepository.findAll();
        }
        Collections.sort(allCohorts);
        model.addAttribute("allCohorts", allCohorts);
        return "/cohort/cohortOverview";
    }

    @GetMapping("/{cohortId}")
    private String getCohortDetails(@PathVariable("cohortId") Long cohortId, Model model) {
        Optional<Cohort> optionalCohort = cohortRepository.findById(cohortId);

        if (optionalCohort.isEmpty()) {
            return "redirect:/cohort/all";
        }

        Cohort cohort = optionalCohort.get();

        List<Student> allStudents = studentRepository.findStudentsByCohortIsNull();
        model.addAttribute("allStudents", allStudents);
        model.addAttribute("cohort", cohort);

        return "/cohort/cohortDetails";
    }

    @GetMapping("/new")
    private String showCreateCohortForm(Model model) {
        Cohort newCohort = new Cohort();
        newCohort.setStartDate(DEFAULT_DATE);
        newCohort.setEndDate(DEFAULT_DATE.plusDays(1));
        model.addAttribute("cohort", newCohort);

        List<Student> allStudents = studentRepository.findStudentsByCohortIsNull();

        model.addAttribute("allStudents", allStudents);
        model.addAttribute("allProgrammes", programmeRepository.findAll());

        return "/cohort/createCohortForm";
    }

    @GetMapping("/edit/{cohortId}")
    private String showEditCohortForm(@PathVariable("cohortId") Long cohortId, Model model) {
        Optional<Cohort> optionalCohort = cohortRepository.findById(cohortId);

        if (optionalCohort.isEmpty()) {
            return "redirect:/cohort/all";
        }

        Cohort cohort = optionalCohort.get();

        //In the edit form, show a list of students that either have no cohort or are in the current cohort
        List<Student> allStudents = studentRepository.findStudentsByCohortIsNull();
        List<Student> studentsCurrentCohort = cohort.getStudents();
        allStudents.addAll(studentsCurrentCohort);

        model.addAttribute("cohort", cohort);
        model.addAttribute("allStudents", allStudents);
        model.addAttribute("allProgrammes", programmeRepository.findAll());

        return "/cohort/createCohortForm";
    }

    @PostMapping("/new")
    private String saveOrUpdateCohort(@ModelAttribute("cohort") Cohort cohortToBeSaved, BindingResult result) {

        if (!result.hasErrors()) {
            removeAllStudentsFromOldCohort(cohortToBeSaved);

            cohortRepository.save(cohortToBeSaved);

            List<Student> students = cohortToBeSaved.getStudents();

            for (Student student : students) {
                student.setCohort(cohortToBeSaved);
            }
        }

        Long cohortId = cohortToBeSaved.getCohortId();

        return "redirect:/cohort/" + cohortId;
    }

    //Checks whether there is a cohort with the same id, empties the cohort attribute of all students in there
    private void removeAllStudentsFromOldCohort(Cohort cohortToBeSaved) {
        if (cohortToBeSaved.getCohortId() != null) {
            Optional<Cohort> optionalOldCohort = cohortRepository.findById(cohortToBeSaved.getCohortId());

            if (optionalOldCohort.isPresent()) {
                Cohort cohort = optionalOldCohort.get();
                cohort.removeAllStudentsFromCohort();
            }
        }
    }

    @PostMapping(value = "/new", params = "cancel")
    private String cancelForm() {

        return "redirect:/cohort/all";
    }

    @GetMapping("/{cohortId}/add/{studentId}")
    private String addStudentToCohort(@PathVariable("studentId") Long studentId,
                                      @PathVariable("cohortId") Long cohortId) {

        Optional<Cohort> optionalCohort = cohortRepository.findById(cohortId);
        Optional<Student> optionalStudent = studentRepository.findById(studentId);

        if (optionalCohort.isEmpty() || optionalStudent.isEmpty()) {
            return "redirect:/cohort/all";
        }

        Cohort cohort = optionalCohort.get();
        Student student = optionalStudent.get();

        student.setCohort(cohort);
        studentRepository.save(student);

        return "redirect:/cohort/" + cohortId;
    }

    @GetMapping("/delete/{cohortId}")
    private String deleteCohort(@PathVariable("cohortId") Long cohortId) {
        Optional<Cohort> optionalCohort = cohortRepository.findById(cohortId);

        if (optionalCohort.isPresent()) {
            Cohort cohort = optionalCohort.get();

            cohort.removeAllStudentsFromCohort();

            cohortRepository.delete(optionalCohort.get());
        }

        return "redirect:/cohort/" + cohortId;
    }
}