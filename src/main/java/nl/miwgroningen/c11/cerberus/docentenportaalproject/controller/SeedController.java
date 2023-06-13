package nl.miwgroningen.c11.cerberus.docentenportaalproject.controller;

import lombok.RequiredArgsConstructor;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.*;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Seed the database with some inital data
 */

@Controller
@RequiredArgsConstructor
public class SeedController {
    private final CohortRepository cohortRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final ProgrammeRepository programmeRepository;

    @GetMapping("/seed")
    private String seedDatabase() {
        Programme programme = new Programme();
        programme.setProgrammeName("Software Engineering");
        Programme programmeTwo = new Programme();
        programmeTwo.setProgrammeName("Data-analyse");
        programmeRepository.save(programme);
        programmeRepository.save(programmeTwo);

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

        Teacher teacherOne = new Teacher();
        teacherOne.setTeacherName("Keenan Mcneil");
        Teacher teacherTwo = new Teacher();
        teacherTwo.setTeacherName("Kiara Floyd");
        teacherRepository.save(teacherOne);
        teacherRepository.save(teacherTwo);

        List<Teacher> teachersOop = new ArrayList<>();
        teachersOop.add(teacherOne);

        Subject oop = new Subject();
        oop.setSubjectName("OOP");
        oop.setTeachers(teachersOop);
        subjectRepository.save(oop);

        return "redirect:/";
    }
}
