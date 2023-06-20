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
    private final CohortRepository cohortRepository;
    private final StudentRepository studentRepository;
    private final ProgrammeRepository programmeRepository;

    @GetMapping("/all")
    private String showAllCohorts(Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Cohort> allCohorts;
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
        Cohort cohort = getCohortWithId(cohortId);
        if (cohort == null) return "redirect:/cohort/all";

        List<Student> allStudents = getListOfStudentsWithoutCohort();
        model.addAttribute("allStudents", allStudents);
        model.addAttribute("cohort", cohort);

        return "/cohort/cohortDetails";
    }

    @GetMapping("/new")
    private String showCreateCohortForm(Model model) {
        //New cohort, but with both dates set to avoid a NullPointerException
        Cohort newCohort = new Cohort();
        newCohort.setStartDate(LocalDate.now());
        newCohort.setEndDate(LocalDate.now().plusDays(1));
        model.addAttribute("cohort", newCohort);

        List<Student> allStudents = getListOfStudentsWithoutCohort();

        model.addAttribute("allStudents", allStudents);
        model.addAttribute("allProgrammes", programmeRepository.findAll());

        return "/cohort/createCohortForm";
    }

    @GetMapping("/edit/{cohortId}")
    private String showEditCohortForm(@PathVariable("cohortId") Long cohortId, Model model) {
        Cohort cohort = getCohortWithId(cohortId);
        if (cohort == null) return "redirect:/cohort/all";

        //In the edit form, show a list of students that either have no cohort or are in the current cohort
        List<Student> allStudents = getListOfStudentsWithoutCohort();
        List<Student> studentsCurrentCohort = cohort.getStudents();
        allStudents.addAll(studentsCurrentCohort);

        model.addAttribute("cohort", cohort);
        model.addAttribute("allStudents", allStudents);
        model.addAttribute("allProgrammes", programmeRepository.findAll());

        return "/cohort/createCohortForm";
    }

    private Cohort getCohortWithId(Long cohortId) {
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

    @PostMapping("/new")
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

    //Finds if there is a cohort with the same id, empties the cohort attribute of all students in there
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

    @PostMapping(value = "/new", params = "cancel")
    private String cancelForm() {

        return "redirect:/cohort/all";
    }

    @GetMapping("/{cohortId}/add/{studentId}")
    private String addStudentToCohort(@PathVariable("studentId") Long studentId,
                                      @PathVariable("cohortId") Long cohortId) {

        Optional<Cohort> optionalCohort = cohortRepository.findById(cohortId);
        Optional<Student> optionalStudent = studentRepository.findById(studentId);

        if(optionalCohort.isEmpty() || optionalStudent.isEmpty()) {
            return "redirect:/cohort/all";
        }

        Cohort cohort = optionalCohort.get();
        Student student = optionalStudent.get();

        cohort.addStudentToCohort(student);
        cohortRepository.save(cohort);

        student.setCohort(cohort);
        studentRepository.save(student);

        return "redirect:/cohort/" + cohortId;
    }

    @GetMapping("/delete/{cohortId}")
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

        return "redirect:/cohort/" + cohortId;
    }
}