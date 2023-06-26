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
    private static final int TEST_AMOUNT = 3;
    private static final int TEST_PARTS_AMOUNT = 7;
    private static final int ASSIGNMENT_AMOUNT = 10;
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
        createRandomTest();
        createRealisticTest();
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

    private void createRandomTest() {
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

        for (int index = 0; index < TEST_PARTS_AMOUNT; index++) {
            int randomSuper = (int) (Math.random() * allRandomSuperTests.size());

            Test randomTestParts = Test.builder().testName(createTestNameWithoutNumber(faker))
                    .testContents(faker.lorem().sentence(5, 15))
                    .superTest(allRandomSuperTests.get(randomSuper)).build();

            testRepository.save(randomTestParts);
        }
    }

    private void createRealisticTest() {
        Test oopOefenTentamenMilieuzone = Test.builder().testName("Controle Milieuzone").testDate(LocalDate.parse("2023-05-25")).build();

        Test milieuZoneDeel1 = Test.builder().testName("Bouw klassenstructuur")
                                            .superTest(oopOefenTentamenMilieuzone).build();
        Test milieuZoneStap0 = Test.builder().testName("Verander de welkomstboodschap")
                                            .testContents("Verander de boodschap zodat je eigen naam verschijnt.")
                                            .superTest(milieuZoneDeel1).build();
        Test milieuZoneStap1 = Test.builder().testName("Klasse Auto")
                                            .testContents("Maak de klasse Auto. Houd hierbij rekening met de volgende eisen:" +
                                                    "\n- De methode isSchoon() geeft aan of een auto milieutechnisch schoon is. Voor de klasse Auto geldt: deze is nooit schoon. Deze methode wordt goed (beter) geïmplementeerd in de subklassen")
                                            .superTest(milieuZoneDeel1).build();
        Test milieuZoneStap2 = Test.builder().testName("Subklasse ElectrischeAuto")
                                            .testContents("Codeer de subklasse ElectrischeAuto. Houd hierbij rekening met de volgende eisen:" +
                                                    "\n- Override de methode isSchoon(). Elke elektrische auto is per definitie schoon." +
                                                    "\n- De methode toString() moet dezelfde output geven als hieronder wordt aangegeven." +
                                                    "\n- Test je code door in de Launcher class de betreffende testcode te 'uncommenten'.")
                                            .superTest(milieuZoneDeel1).build();
        Test milieuZoneStap3 = Test.builder().testName("Subklasse BrandstofAuto")
                                            .testContents("Codeer de subklasse BrandstofAuto. Houd hierbij rekening met de volgende eisen:" +
                                                    "\n- Override de methode isSchoon(). Een brandstofauto is schoon als het bouwjaar 1995 of nieuwer is, en het gewicht lichter is dan 2500 en de uitstoot schoon is. Het hangt af van het soort brandstof hoeveel uitstoot “schoon” is: een diesel is schoon bij een uitstoot van maximaal 280, en een schone benzine auto heeft maximaal een uitstoot van 320. Een auto met brandstof gas kan nooit schoon zijn." +
                                                    "\n- De methode toString() moet dezelfde output geven als hieronder wordt aangegeven." +
                                                    "\n- Test je code door in de Launcher class de betreffende testcode te “uncommenten”.")
                                            .superTest(milieuZoneDeel1).build();
        Test milieuZoneStap4 = Test.builder().testName("Klasse MilieuControle")
                                            .testContents("Maak de klasse MilieuControle volgens het class diagram, zodat je auto’s aan een controlelijst kunt toevoegen.")
                                            .superTest(milieuZoneDeel1).build();
        Test milieuZoneDeel2 = Test.builder().testName("Gebruik klassen, bestand en database")
                                            .superTest(oopOefenTentamenMilieuzone).build();
        Test milieuZoneStap5 = Test.builder().testName("Print alle auto’s die de milieuzone in mogen")
                                            .testContents("Breidt de MilieuZone klasse uit met een methode die alle auto’s afdrukt die de milieuzone in mogen. Een auto mag de milieuzone in wanneer deze schoon is." +
                                                    "\nDe signatuur van deze methode is als volgt: public void printSchoneAutos()")
                                            .superTest(milieuZoneDeel2).build();
        Test milieuZoneStap6 = Test.builder().testName("Print bedrijfsauto’s met een bepaald type brandstof naar een bestand")
                                            .testContents("Implementeer de methode printBedrijfsautosMetType(String brandstof)in klasse MilieuControle. De methode print alle bedrijfsauto’s met een bepaald brandstoftype naar een bestand. " +
                                                    "Zorg dat in de methode een tekstbestand gemaakt wordt met de naam bedrijfsautos.txt, waarin de juiste bedrijfsauto’s worden weggeschreven. Sla dit bestand op in de folder ‘resources’ van je project (met pad ‘src/main/resources/’). " +
                                                    "\nLet wel: Als de eigenaar een bedrijf is, dan is sprake van een bedrijfsauto.")
                                            .superTest(milieuZoneDeel2).build();
        Test milieuZoneStap7 = Test.builder().testName("Haal een lijst met auto’s uit de database")
                                            .testContents("Er is een begin gemaakt met een database genaamd ‘Milieuzone’. Daarin staat een tabel ‘Auto’ met een aantal auto’s. " +
                                                    "Maak een AutoDAO klasse die het mogelijk maakt om alle auto’s met een bepaald bouwjaar uit de database te halen.")
                                            .superTest(milieuZoneDeel2).build();

        testRepository.save(oopOefenTentamenMilieuzone);
        testRepository.save(milieuZoneDeel1);
        testRepository.save(milieuZoneStap0);
        testRepository.save(milieuZoneStap1);
        testRepository.save(milieuZoneStap2);
        testRepository.save(milieuZoneStap3);
        testRepository.save(milieuZoneStap4);
        testRepository.save(milieuZoneDeel2);
        testRepository.save(milieuZoneStap5);
        testRepository.save(milieuZoneStap6);
        testRepository.save(milieuZoneStap7);
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

    private String createTestNameWithoutNumber(Faker faker) {
        StringBuilder testNameWithNumber = new StringBuilder(faker.educator().subjectWithNumber());
        StringBuilder testNameWithoutNumber = testNameWithNumber.delete(testNameWithNumber.length() - 4, testNameWithNumber.length())
                .append(" ").append(faker.verb().ingForm());
        return testNameWithoutNumber.toString();
    }
}
