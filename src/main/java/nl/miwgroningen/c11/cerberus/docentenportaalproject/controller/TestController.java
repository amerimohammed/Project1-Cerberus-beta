package nl.miwgroningen.c11.cerberus.docentenportaalproject.controller;

import lombok.RequiredArgsConstructor;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Test;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.TestRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

/**
 * Handels all actions concerning tests.
 *
 * @author Marianne Kooistra, Mohammed Almameri, Joost Schreuder
 */

@Controller
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {
    private final TestRepository testRepository;

    @GetMapping("/all")
    private String showTestOverview(Model model) {
        model.addAttribute("allTests", testRepository.findByOrderByTestDate());

        return "testPages/testOverview";
    }

    @GetMapping("/delete/{testId}")
    private String deleteTest(@PathVariable("testId") Long testId, Model model) {
        Optional<Test> optionalTest = testRepository.findById(testId);

        if (optionalTest.isPresent()) {
            testRepository.delete(optionalTest.get());
        }

        return "redirect:/test/all";
    }
}
