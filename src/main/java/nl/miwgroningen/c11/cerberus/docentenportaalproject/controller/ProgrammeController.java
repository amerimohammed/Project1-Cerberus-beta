package nl.miwgroningen.c11.cerberus.docentenportaalproject.controller;

import lombok.RequiredArgsConstructor;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Image;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Programme;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.ImageRepository;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.ProgrammeRepository;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.SubjectRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

/**
 * Handles all actions concerning programme.
 *
 * @author Marianne Kooistra, Mohammed Almameri, Joost Schreuder
 */

@Controller
@RequiredArgsConstructor
public class ProgrammeController {
    private final ProgrammeRepository programmeRepository;
    private final SubjectRepository subjectRepository;
    private final ImageRepository imageRepository;
    private final String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/uploads";

    @GetMapping({"/", "/home"})
    private String showHomePage(Model model) {
        model.addAttribute("allProgrammes", programmeRepository.findAll());

        return "HomePage";
    }

    @GetMapping("/programme/new")
    private String showCreateProgrammeForm(Model model) {
        model.addAttribute("allSubjects", subjectRepository.findAll());
        model.addAttribute("programme", new Programme());

        return "programme/createProgrammeForm";
    }

    @GetMapping("programme/edit/{programmeId}")
    private String showEditProgrammeForm(@PathVariable("programmeId") Long programmeId, Model model) {
        Optional<Programme> optionalProgramme = programmeRepository.findById(programmeId);

        if (optionalProgramme.isPresent()) {
            model.addAttribute("programme", optionalProgramme.get());
            model.addAttribute("allSubjects", subjectRepository.findAll());

            return "programme/createProgrammeForm";
        }

        return "redirect:/home";
    }

    @PostMapping("/programme/new")
    private String saveOrUpdateProgramme(@ModelAttribute("programme") Programme programmeToBeSaved,
                                         @RequestParam("imageFile") MultipartFile file,
                                         BindingResult result) {

        if (!result.hasErrors()) {
            if (!file.isEmpty()) {
                try {
                    Image image = saveImage(file);
                    programmeToBeSaved.setImage(image);
                } catch (IOException exception) {
                    System.err.println(exception.getMessage());
                }
            }
            programmeRepository.save(programmeToBeSaved);
        }

        return "redirect:/home";
    }

    @PostMapping(value = "/programme/new", params = "cancel")
    private String cancelForm() {

        return "redirect:/home";
    }

    @GetMapping("/home/delete/{programmeId}")
    private String deleteProgramme(@PathVariable("programmeId") Long programmeId, Model model) {
        Optional<Programme> optionalProgramme = programmeRepository.findById(programmeId);

        if(optionalProgramme.isPresent()) {
            try {
                programmeRepository.delete(optionalProgramme.get());
            } catch (DataIntegrityViolationException dataIntegrityViolationException) {
                System.out.println(dataIntegrityViolationException.getMessage());
                model.addAttribute("errorMessage",
                        "This Programme can't be deleted due to relation to other entities");
                return "error";
            }
        }

        return "redirect:/home";
    }

    @GetMapping("/programme/view/{programmeId}")
    private String showProgrammeDetails(@PathVariable("programmeId") Long programmeId, Model model) {
        Optional<Programme> optionalProgramme = programmeRepository.findById(programmeId);
        if (optionalProgramme.isPresent()) {
            model.addAttribute("programme", optionalProgramme.get());

            return "programme/programmeDetails";
        }

        return "redirect:/programme/all";
    }

    private Image saveImage(MultipartFile file) throws IOException {
        StringBuilder fileName = new StringBuilder();
        fileName.append(UUID.randomUUID());
        if (getExtensionByStringHandling(file.getOriginalFilename()).isPresent()) {
            fileName.append(".").append(getExtensionByStringHandling(file.getOriginalFilename()).get());
        }
        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, fileName.toString());
        Files.write(fileNameAndPath, file.getBytes());

        Image image = new Image();
        image.setImageName(fileName.toString());
        return imageRepository.save(image);
    }

    private Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

}
