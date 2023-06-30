package nl.miwgroningen.c11.cerberus.docentenportaalproject.controller;

import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.*;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Seeds the database with some initial data.
 *
 * @author Marianne Kooistra, Mohammed Almameri, Joost Schreuder
 */

@Controller
@RequiredArgsConstructor
public class SeedController {
    private static final int SUBJECT_AMOUNT = 10;
    private static final int TEACHER_AMOUNT = 5;
    private static final int PROGRAMME_AMOUNT = 5;
    private static final int COHORT_AMOUNT = 10;
    private static final int STUDENT_AMOUNT = 50;
    private static final int TEST_AMOUNT = 3;
    private static final int TEST_PARTS_AMOUNT = 7;
    private static final int ASSIGNMENT_AMOUNT = 10;
    private static final int SUBJECTS_IN_PROGRAMME_AMOUNT = 5;

    private static final int SUBJECT_MAX_DURATION = 10;
    private static final int SUBJECT_MIN_DURATION = 1;
    private static final int DAYS_RANGE = 1000;

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
        createEntities();
        connectEntities();

        return "redirect:/";
    }

    private void createEntities() {
        createSubject();
        createTeacher();
        createProgramme();
        createCohort();
        createStudent();
        createRandomTest();
        createRealisticTest();
        createAssignment();
    }

    private void connectEntities() {
        assignSubjectsToProgrammes();
        assignProgrammesToCohorts();
        assignTeachersToSubjects();
        assignCohortsToStudents();
        assignSubjectToTest();
        assignSubjectToAssignment();
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
            createUserAccount(teacher, "teacher");

            Optional<Role> teacherRole = roleRepository.findRoleByRoleName("TEACHER");
            if (teacherRole.isPresent()) {
                Set<Role> teacherRoles = new HashSet<>();
                teacherRoles.add(teacherRole.get());
                teacher.setRoles(teacherRoles);
            }

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
            Date startDate = faker.date().past(DAYS_RANGE, TimeUnit.DAYS);
            Date endDate = faker.date().future(DAYS_RANGE, TimeUnit.DAYS);

            LocalDate startLocalDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            Cohort cohort = new Cohort(startLocalDate, endLocalDate);

            cohortRepository.save(cohort);
        }
    }

    private void createStudent() {
        for (int index = 0; index < SeedController.STUDENT_AMOUNT; index++) {
            Student student = new Student();
            createUserAccount(student, "student");

            Optional<Role> studentRole = roleRepository.findRoleByRoleName("STUDENT");
            if (studentRole.isPresent()) {
                Set<Role> studentRoles = new HashSet<>();
                studentRoles.add(studentRole.get());
                student.setRoles(studentRoles);
            }

            studentRepository.save(student);
        }
    }

    private void createRandomTest() {
        List<Test> allRandomSuperTests = createTests();
        createTestParts(allRandomSuperTests);
    }

    private List<Test> createTests() {
        Faker faker = new Faker();
        List<Test> allRandomSuperTests = new ArrayList<>();

        for (int index = 0; index < TEST_AMOUNT; index++) {
            Date futureDate = faker.date().future(1000, TimeUnit.DAYS);
            LocalDate futureLocalDate = futureDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            Test randomTest = Test.builder().testName(createTestNameWithoutNumber(faker))
                    .testDate(futureLocalDate).build();

            allRandomSuperTests.add(randomTest);
            testRepository.save(randomTest);
        }
        return allRandomSuperTests;
    }

    private void createTestParts(List<Test> allRandomSuperTests) {
        Faker faker = new Faker();

        for (int index = 0; index < TEST_PARTS_AMOUNT; index++) {
            int randomSuper = (int) (Math.random() * allRandomSuperTests.size());

            Test randomTestParts = Test.builder().testName(createTestNameWithoutNumber(faker))
                    .testContents(faker.lorem().sentence(5, 15))
                    .superTest(allRandomSuperTests.get(randomSuper)).build();

            testRepository.save(randomTestParts);
        }
    }

    private void createRealisticTest() {
        Test oopOefenTentamenMilieuzone = createRealisticSuperTest();

        List<Test> testPartList = new ArrayList<>();
        testPartList.add(oopOefenTentamenMilieuzone);

        File file = new File("src/main/resources/static/csv/test.csv");

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String[] testPartString = scanner.nextLine().split(";");
                createTestPartFromString(testPartList, testPartString);
            }
        } catch (FileNotFoundException errorMessage) {
            System.err.println("File not found.");
        }
    }

    private Test createRealisticSuperTest() {
        Test oopOefenTentamenMilieuzone = Test.builder().testName("Controle Milieuzone")
                .testDate(LocalDate.parse("2023-05-25")).build();
        testRepository.save(oopOefenTentamenMilieuzone);
        return oopOefenTentamenMilieuzone;
    }

    private void createTestPartFromString(List<Test> testPartList, String[] testPartString) {
        int superTestIndex = Integer.parseInt(testPartString[2]);

        Test testPart = Test.builder().testName(testPartString[0]).testContents(testPartString[1])
                .superTest(testPartList.get(superTestIndex)).build();

        testRepository.save(testPart);
        testPartList.add(testPart);
    }

    private void createAssignment() {
        Faker faker = new Faker();

        for (int index = 0; index < ASSIGNMENT_AMOUNT; index++) {
            Assignment assignment = Assignment.builder()
                    .assignmentName(faker.lorem().sentence(1, 3)).build();

            assignmentRepository.save(assignment);
        }
    }

    private void assignSubjectsToProgrammes() {
        List<Subject> subjects = subjectRepository.findAll();
        List<Programme> programmes = programmeRepository.findAll();

        if (subjects.size() < 1) {
            return;
        }

        for (Programme programme : programmes) {
            Set<Subject> programmeSubjects = new HashSet<>();

            for (int subject = 0; subject < SeedController.SUBJECTS_IN_PROGRAMME_AMOUNT; subject++) {
                int randomSubject = (int) (Math.random() * subjects.size());
                programmeSubjects.add(subjects.get(randomSubject));
            }
            programme.setSubjects(programmeSubjects);
        }
    }

    private void assignProgrammesToCohorts() {
        List<Cohort> cohorts = cohortRepository.findAll();
        List<Programme> programmes = programmeRepository.findAll();

        if (programmes.size() < 1) {
            return;
        }

        for (Cohort cohort : cohorts) {
            int randomProgramme = (int) (Math.random() * programmes.size());
            cohort.setProgramme(programmes.get(randomProgramme));
            cohortRepository.save(cohort);
        }
    }

    private void assignTeachersToSubjects() {
        List<Subject> subjects = subjectRepository.findAll();
        List<Teacher> allTeachers = teacherRepository.findAll();

        if (allTeachers.size() < 1) {
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

    private void assignCohortsToStudents() {
        List<Cohort> cohorts = cohortRepository.findAll();
        List<Student> students = studentRepository.findAll();

        if (cohorts.size() < 1) {
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

        if (subjects.size() < 1) {
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
            assignments.remove(testPart);
        }

        for (Assignment assignment : assignments) {
            int randomSubject = (int) (Math.random() * subjects.size());
            assignment.setSubject(subjects.get(randomSubject));

            assignmentRepository.save(assignment);
        }
    }

    private void createUserAccount(User user, String password) {
        user.setFullName(createFakeName());
        user.generateUsernameAndPassword(userRepository);
        user.setPassword(password);
        user.hashPassword();
    }

    private String createFakeName() {
        Faker faker = new Faker();

        return faker.name().firstName() + " " + faker.name().lastName();
    }

    private String createTestNameWithoutNumber(Faker faker) {
        StringBuilder testNameWithNumber = new StringBuilder(faker.educator().subjectWithNumber());
        StringBuilder testNameWithoutNumber = testNameWithNumber.delete(testNameWithNumber.length() - 4, testNameWithNumber.length())
                .append(" ").append(faker.verb().ingForm());
        return testNameWithoutNumber.toString();
    }
}
