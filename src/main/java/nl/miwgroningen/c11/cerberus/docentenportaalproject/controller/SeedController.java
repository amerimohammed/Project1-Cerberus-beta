package nl.miwgroningen.c11.cerberus.docentenportaalproject.controller;

import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.*;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
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
    private static final int TEST_AMOUNT = 10;
    private static final int ASSIGNMENT_AMOUNT = 15;
    private static final int SUBJECTS_IN_PROGRAMME_AMOUNT = 5;
    private static final int SUBJECT_MAX_DURATION = 10;
    private static final int SUBJECT_MIN_DURATION = 1;

    private final CohortRepository cohortRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final ProgrammeRepository programmeRepository;
    private final UserRepository userRepository;
    private final TestRepository testRepository;
    private final RoleRepository roleRepository;
    private final AssignmentRepository assignmentRepository;

    @GetMapping("/seed")
    private String seedDatabase() {
        createSubject();
        createTeacher();
        createProgramme();
        createCohort();
        createStudent();
        createTest();
        createAssignment();

        assignSubjectsToProgrammes();
        assignProgrammesToCohorts();
        assignTeachersToSubjects();
        assignCohortsToStudents();
        assignSubjectToTest();
        assignSubjectToAssignment();

        return "redirect:/";
    }

    private void createSubject() {
        Faker faker = new Faker();

        for (int index = 0; index < SeedController.SUBJECT_AMOUNT; index++) {
            Subject subject = new Subject();
            subject.setSubjectName(faker.educator().subjectWithNumber());
            subject.setDurationWeeks((int) ((Math.random() * SUBJECT_MAX_DURATION) + SUBJECT_MIN_DURATION));

            subjectRepository.save(subject);
        }
    }

    private void createTeacher() {
        for (int index = 0; index < SeedController.TEACHER_AMOUNT; index++) {
            Teacher teacher = new Teacher();
            teacher.setFullName(createFakeName());
            teacher.generateUsernameAndPassword(userRepository);
            teacher.setPassword("teacher");

            Optional<Role> teacherRole = roleRepository.findRoleByRoleName("TEACHER");
            if(teacherRole.isPresent()){
                Set<Role> teacherRoles = new HashSet<>();
                teacherRoles.add(teacherRole.get());
                teacher.setRoles(teacherRoles);
            }

            teacher.hashPassword();
            teacherRepository.save(teacher);
        }
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

    private void createCohort() {
        Faker faker = new Faker();

        for (int index = 0; index < SeedController.COHORT_AMOUNT; index++) {
            Date startDate = faker.date().past(1000, TimeUnit.DAYS);
            Date endDate = faker.date().future(1000, TimeUnit.DAYS);

            LocalDate startLocalDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            Cohort cohort = new Cohort(startLocalDate, endLocalDate);

            cohortRepository.save(cohort);
        }
    }

    private void createStudent() {
        for (int index = 0; index < SeedController.STUDENT_AMOUNT; index++) {
            Student student = new Student();
            student.setFullName(createFakeName());
            student.generateUsernameAndPassword(userRepository);
            student.setPassword("student");
            student.hashPassword();

            Optional<Role> studentRole = roleRepository.findRoleByRoleName("STUDENT");
            if(studentRole.isPresent()){
                Set<Role> studentRoles = new HashSet<>();
                studentRoles.add(studentRole.get());
                student.setRoles(studentRoles);
            }

            studentRepository.save(student);
        }
    }

    //Working on changing this entity
    // TODO: make this shorter and simpler.
    private void createTest() {
        Faker faker = new Faker();

        Test test = new Test();
        Test testTwo = new Test();
        Test testThree = new Test();
        Test testFour = new Test();
        Test testFive = new Test();

        //Create the test name
        StringBuilder testNameWithNumber = new StringBuilder(faker.educator().subjectWithNumber());
        StringBuilder testNameWithoutNumber = testNameWithNumber.delete(testNameWithNumber.length() - 4, testNameWithNumber.length());
        String testName = testNameWithoutNumber + " " + faker.verb().ingForm();
        StringBuilder testNameWithNumberTwo = new StringBuilder(faker.educator().subjectWithNumber());
        StringBuilder testNameWithoutNumberTwo = testNameWithNumberTwo.delete(testNameWithNumberTwo.length() - 4, testNameWithNumberTwo.length());
        String testNameTwo = testNameWithoutNumberTwo + " " + faker.verb().ingForm();
        StringBuilder testNameWithNumberThree = new StringBuilder(faker.educator().subjectWithNumber());
        StringBuilder testNameWithoutNumberThree = testNameWithNumberThree.delete(testNameWithNumberThree.length() - 4, testNameWithNumberThree.length());
        String testNameThree = testNameWithoutNumberThree + " " + faker.verb().ingForm();

        //Assign name to test
        test.setTestName(testName);
        testTwo.setTestName(testNameTwo);
        testThree.setTestName(testNameThree);
        testFour.setTestName("testing main test");
        testFive.setTestName("testing sub test part");

        //Set date
        Date futureDate = faker.date().future(1000, TimeUnit.DAYS);
        LocalDate futureLocalDate = futureDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        test.setTestDate(futureLocalDate);
        testFour.setTestDate(LocalDate.now());

        testTwo.setTestContents("This is a description, as a place holder.");
        testThree.setTestContents("Change this to lorum ipsum with the faker.");
        testFive.setTestContents("This is a description, as a place holder. Change this to lorum ipsum with the faker.");

        //Set superTest
        testTwo.setSuperTest(test);
        testThree.setSuperTest(test);
        testFive.setSuperTest(testFour);

        testRepository.save(test);
        testRepository.save(testTwo);
        testRepository.save(testThree);
        testRepository.save(testFour);
        testRepository.save(testFive);
    }

    private void createAssignment() {
        Faker faker = new Faker();

        for (int index = 0; index < ASSIGNMENT_AMOUNT; index++) {
            Assignment assignment = Assignment.builder()
                    .assignmentName(faker.lorem().sentence(1, 3)).build();

            assignmentRepository.save(assignment);
        }
    }

    //Assign random subjects to programmes
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

    //Assign a random programme to each cohort
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

    //Assign a random teacher to each subject
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

    //Assign a random cohort to each student
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

    private void assignSubjectToTest() {
        List<Subject> subjects = subjectRepository.findAll();
        List<Test> tests = testRepository.findBySuperTestIsNull();

        if(subjects.size() < 1) {
            return;
        }

        for (Test test : tests) {
            int randomSubject = (int) (Math.random() * subjects.size());
            test.setSubject(subjects.get(randomSubject));

            testRepository.save(test);
        }
    }

    private void assignSubjectToAssignment() {
        List<Subject> subjects = subjectRepository.findAll();
        List<Assignment> assignments = assignmentRepository.findAll();
        List<Test> testParts = testRepository.findBySuperTestIsNotNull();

        if (subjects.size() < 1) {
            return;
        }

        //Make sure the test parts do not get a subject assigned
        for (Test testPart : testParts) {
            if (assignments.contains(testPart)) {
                assignments.remove(testPart);
            }
        }

        for (Assignment assignment : assignments) {
            int randomSubject = (int) (Math.random() * subjects.size());
            assignment.setSubject(subjects.get(randomSubject));

            assignmentRepository.save(assignment);
        }
    }

    private String createFakeName() {
        Faker faker = new Faker();

        return faker.name().firstName() + " " + faker.name().lastName();
    }
}
