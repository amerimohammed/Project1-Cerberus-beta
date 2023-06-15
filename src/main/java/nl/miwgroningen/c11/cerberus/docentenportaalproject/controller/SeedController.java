package nl.miwgroningen.c11.cerberus.docentenportaalproject.controller;

import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.*;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Seed the database with some initial data
 */

@Controller
@RequiredArgsConstructor
public class SeedController {
    private static final int SUBJECT_AMOUNT = 10;
    private static final int TEACHER_AMOUNT = 5;
    private static final int PROGRAMME_AMOUNT = 5;
    private static final int COHORT_AMOUNT = 10;
    private static final int STUDENT_AMOUNT = 50;
    private static final int SUBJECTS_IN_PROGRAMME_AMOUNT = 5;

    private final CohortRepository cohortRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final ProgrammeRepository programmeRepository;

    @GetMapping("/seed")
    private String seedDatabase() {
        createSubject();
        createTeacher();
        createProgramme();
        createCohort();
        createStudent();

        assignSubjectsToProgrammes();
        assignProgrammesToCohorts();
        assignTeachersToSubjects();
        assignCohortsToStudents();

        return "redirect:/";
    }

    private void createProgramme() {
        Faker faker = new Faker();

        for (int index = 0; index < SeedController.PROGRAMME_AMOUNT; index++) {
            Programme programme = Programme.builder()
                    .programmeName(faker.educator().course())
                    .shortDescription(faker.lorem().paragraph(1))
                    .build();

            programmeRepository.save(programme);
        }
    }

    //Randomize the subjects of programmes
    private void assignSubjectsToProgrammes() {
        List<Subject> subjects = subjectRepository.findAll();
        List<Programme> programmes = programmeRepository.findAll();

        if (subjects.size() < 1) {
            return;
        }

        for (Programme programme : programmes) {
            List<Subject> programmeSubjects = new ArrayList<>();

            for (int subject = 0; subject < SeedController.SUBJECTS_IN_PROGRAMME_AMOUNT; subject++) {
                int randomSubject = (int) (Math.random() * subjects.size());
                programmeSubjects.add(subjects.get(randomSubject));
            }
            programme.setSubjects(programmeSubjects);
        }
    }

    private void createCohort() {
        Faker faker = new Faker();

        for (int index = 0; index < SeedController.COHORT_AMOUNT; index++) {
            Date startDate = faker.date().past(1000, TimeUnit.DAYS);
            Date endDate = faker.date().future(1000, TimeUnit.DAYS);

            LocalDate startLocalDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            String cohortName = faker.color().name();
            cohortName = cohortName.substring(0, 1).toUpperCase() + cohortName.substring(1);

            Cohort cohort = Cohort.builder()
                    .cohortName(cohortName)
                    .startDate(startLocalDate)
                    .endDate(endLocalDate)
                    .build();

            cohortRepository.save(cohort);
        }
    }

    //Randomize the programme of cohorts
    private void assignProgrammesToCohorts() {
        List<Cohort> cohorts = cohortRepository.findAll();
        List<Programme> programmes = programmeRepository.findAll();

        if(programmes.size() < 1) {
            return;
        }

        for (Cohort cohort : cohorts) {
            int randomProgramme = (int) (Math.random() * programmes.size());
            cohort.setProgramme(programmes.get(randomProgramme));
            cohortRepository.save(cohort);
        }
    }

    private void createTeacher() {
        for (int index = 0; index < SeedController.TEACHER_AMOUNT; index++) {
            Teacher teacher = new Teacher();
            teacher.setTeacherName(createFakeName());

            teacherRepository.save(teacher);
        }
    }

    private void createSubject() {
        Faker faker = new Faker();

        for (int index = 0; index < SeedController.SUBJECT_AMOUNT; index++) {
            Subject subject = new Subject();
            subject.setSubjectName(faker.educator().subjectWithNumber());

            subjectRepository.save(subject);
        }
    }

    //Randomize the teachers of subjects
    private void assignTeachersToSubjects() {
        List<Subject> subjects = subjectRepository.findAll();
        List<Teacher> allTeachers = teacherRepository.findAll();

        if(allTeachers.size() < 1) {
            return;
        }

        for (Subject subject : subjects) {
            List<Teacher> subjectTeachers = new ArrayList<>();

            int randomTeacher = (int) (Math.random() * allTeachers.size());
            subjectTeachers.add(allTeachers.get(randomTeacher));

            subject.setTeachers(subjectTeachers);

            subjectRepository.save(subject);
        }
    }

    private void createStudent() {
        for (int index = 0; index < SeedController.STUDENT_AMOUNT; index++) {

            Student student = new Student();
            student.setStudentName(createFakeName());

            studentRepository.save(student);
        }
    }

    //Randomize the cohorts of students
    private void assignCohortsToStudents() {
        List<Cohort> cohorts = cohortRepository.findAll();
        List<Student> students = studentRepository.findAll();

        if(cohorts.size() < 1) {
            return;
        }

        for (Student student : students) {
            int randomCohort = (int) (Math.random() * cohorts.size());
            student.setCohort(cohorts.get(randomCohort));
            studentRepository.save(student);
        }
    }

    private String createFakeName() {
        Faker faker = new Faker();

        return faker.name().firstName() + " " + faker.name().lastName();
    }
}
