package nl.miwgroningen.c11.cerberus.docentenportaalproject.controller;

import lombok.RequiredArgsConstructor;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Role;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Subject;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Teacher;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.User;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.RoleRepository;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.SubjectRepository;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.TeacherRepository;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Handles all pages of the teacher
 *
 * @author Marianne Kooistra, Mohammed Almameri, Joost Schreuder
 */

@Controller
@RequiredArgsConstructor
@RequestMapping("/teacher")
public class TeacherController {
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @GetMapping({"", "/all"})
    private String showTeacherOverview(Model model) {
        model.addAttribute("allTeachers", teacherRepository.findAll());

        return "teacher/teacherOverview";
    }

    @GetMapping("/new")
    private String showCreateTeacherForm(Model model) {
        List<Subject> allSubjects = subjectRepository.findAll();

        model.addAttribute("teacher", new Teacher());
        model.addAttribute("allSubjects", allSubjects);

        return "teacher/createTeacherForm";
    }

    @GetMapping("/edit/{teacherId}")
    private String showEditTeacherForm(@PathVariable("teacherId") Long teacherId, Model model) {
        Optional<Teacher> optionalTeacher = teacherRepository.findById(teacherId);

        if (optionalTeacher.isPresent()) {
            List<Subject> allSubjects = subjectRepository.findAll();
            Teacher teacher = optionalTeacher.get();

            model.addAttribute("allSubjects", allSubjects);
            model.addAttribute("teacherSubjects", subjectRepository.findAllByTeachersContains(teacher));
            model.addAttribute("teacher", teacher);

            return "teacher/createTeacherForm";
        }

        return "redirect:/teacher/all";
    }

    @PostMapping("")
    private String saveOrUpdateTeacher(@ModelAttribute("teacher")
                                           Teacher teacherToBeSaved, BindingResult result, Model model) {

        if (!result.hasErrors()) {
            if (teacherToBeSaved.getUserId() == null) {
                teacherToBeSaved.generateUsernameAndPassword(userRepository);
                String tempPassword = teacherToBeSaved.getPassword();
                teacherToBeSaved.hashPassword();
                Optional<Role> teacherRole = roleRepository.findRoleByRoleName("TEACHER");
                if(teacherRole.isPresent()){
                    Set<Role> teacherRoles = new HashSet<>();
                    teacherRoles.add(teacherRole.get());
                    teacherToBeSaved.setRoles(teacherRoles);
                }
                teacherRepository.save(teacherToBeSaved);
                model.addAttribute("username", teacherToBeSaved.getUsername());
                model.addAttribute("password", tempPassword);

                return "teacher/teacherAccount";
            } else {
                Optional<Teacher> storedTeacher = teacherRepository.findById(teacherToBeSaved.getUserId());
                if (storedTeacher.isPresent()) {
                    teacherToBeSaved.setUsername(storedTeacher.get().getUsername());
                    teacherToBeSaved.setPassword(storedTeacher.get().getPassword());
                    teacherToBeSaved.setRoles(storedTeacher.get().getRoles());
                    teacherRepository.save(teacherToBeSaved);

                    //Update subjects - first erase from all subjects
                    //Then add teacher to subjects according to new list
                    removeTeacherFromAllSubjects(storedTeacher.get());
                    addTeacherToSubjects(teacherToBeSaved, teacherToBeSaved.getSubjects());
                }
            }
        }

        return "redirect:/teacher/all";
    }

    private void removeTeacherFromAllSubjects(Teacher teacher) {
        List<Subject> teacherSubjects = subjectRepository.findAllByTeachersContains(teacher);

        for (Subject subject : teacherSubjects) {
            subject.removeTeacher(teacher);
            subjectRepository.save(subject);
        }
    }

    private void addTeacherToSubjects(Teacher teacher, List<Subject> subjects) {
        for (Subject subject : subjects) {
            subject.addTeacher(teacher);
            subjectRepository.save(subject);
        }
    }

    @PostMapping(value = "", params = "cancel")
    private String cancelForm() {

        return "redirect:/teacher/all";
    }

    @GetMapping("/delete/{teacherId}")
    private String deleteTeacher(@PathVariable("teacherId") Long teacherId, Model model) {
        Optional<Teacher> optionalTeacher = teacherRepository.findById(teacherId);
        Optional<User> optionalUser = userRepository.findById(teacherId);

        if (optionalTeacher.isPresent() && optionalUser.isPresent()) {
            try {
            userRepository.delete(optionalUser.get());
            teacherRepository.delete(optionalTeacher.get());
            } catch (DataIntegrityViolationException dataIntegrityViolationException) {
                System.out.println(dataIntegrityViolationException.getMessage());
                model.addAttribute("errorMessage",
                        "This Teacher can't be deleted due to relation to other entities");
                return "error";
            }
        }

        return "redirect:/teacher/all";
    }
}
