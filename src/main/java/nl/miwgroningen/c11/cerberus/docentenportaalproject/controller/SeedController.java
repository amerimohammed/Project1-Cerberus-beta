package nl.miwgroningen.c11.cerberus.docentenportaalproject.controller;

import lombok.RequiredArgsConstructor;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Cohort;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Student;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Subject;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.CohortRepository;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.StudentRepository;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.SubjectRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

/**
 * Seed the database with some inital data
 */

@Controller
@RequiredArgsConstructor
public class SeedController {
    private final CohortRepository cohortRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    @GetMapping("/seed")
    private String seedDatabase() {
        Cohort cohort11 = new Cohort();
        cohort11.setCohortName("Cohort 11");
        cohort11.setStartDate(LocalDate.of(2023, 3, 1));
        cohort11.setEndDate(LocalDate.of(2023, 11, 20));
        Cohort cohort12 = new Cohort();
        cohort12.setCohortName("Cohort 12");
        cohort12.setStartDate(LocalDate.of(2023, 8, 2));
        cohort12.setEndDate(LocalDate.of(2024, 2, 15));
        cohortRepository.save(cohort11);
        cohortRepository.save(cohort12);

        Student eersteStudent = new Student();
        eersteStudent.setStudentName("Kees de Vries");
        eersteStudent.setCohort(cohort11);
        Student tweedeStudent = new Student();
        tweedeStudent.setStudentName("Clara Bakker");
        tweedeStudent.setCohort(cohort11);
        Student derdeStudent = new Student();
        derdeStudent.setStudentName("John Doe");
        derdeStudent.setCohort(cohort11);
        Student vierdeStudent = new Student();
        vierdeStudent.setStudentName("Jane Do");
        vierdeStudent.setCohort(cohort12);
        Student vijfdeStudent = new Student();
        vijfdeStudent.setStudentName("Henk Bouwer");
        studentRepository.save(eersteStudent);
        studentRepository.save(tweedeStudent);
        studentRepository.save(derdeStudent);
        studentRepository.save(vierdeStudent);
        studentRepository.save(vijfdeStudent);

        Subject oop = new Subject();
        oop.setSubjectName("OOP");
        //Add teachers here when teacher repository is made
        subjectRepository.save(oop);

        return "redirect:/";
    }
}
