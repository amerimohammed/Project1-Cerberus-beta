package nl.miwgroningen.c11.cerberus.docentenportaalproject.controller;

import lombok.RequiredArgsConstructor;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.dto.subTestDTO;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Test;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.SubjectRepository;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.repository.TestRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
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
    private final SubjectRepository subjectRepository;

    @GetMapping("/all")
    private String showTestOverview(Model model) {
        List<Test> allTests = testRepository.findBySuperTestIsNull();
        Collections.sort(allTests);
        model.addAttribute("allSuperTest", allTests);

        return "testPages/testOverview";
    }

    @GetMapping("/{testId}")
    private String getTestDetails(@PathVariable("testId") Long testId, Model model) {
        Optional<Test> optionalTest = testRepository.findById(testId);

        if (optionalTest.isPresent()) {
            model.addAttribute("superTest", optionalTest.get());
        }

        return "/testPages/testDetails";
    }

    @GetMapping("/new")
    private String showCreateTestForm(Model model) {
        model.addAttribute("test", new Test());
        model.addAttribute("allSubjects", subjectRepository.findAll());

        return "testPages/createTestForm";
    }

    @GetMapping("/new/contents/{superTestId}")
    private String showCreateTestPartForm(@PathVariable("superTestId") Long superTestId, Model model) {
        Optional<Test> optionalTest = testRepository.findById(superTestId);

        if (optionalTest.isPresent()) {
            model.addAttribute("testPartTest", new Test());
            model.addAttribute("subTestDto", subTestDTO.builder().superTest(optionalTest.get()).build());

            return "testPages/createTestPartForm";
        }

        return "redirect:/test/all";
    }

    @GetMapping("/edit/{testId}")
    private String showEditTestForm(@PathVariable("testId") Long testId, Model model) {
        Optional<Test> optionalTest = testRepository.findById(testId);

        if (optionalTest.isPresent()) {
            model.addAttribute("test", optionalTest.get());
            model.addAttribute("allSubjects", subjectRepository.findAll());
            return "testPages/createTestForm";
        }

        return "redirect:/test/all";
    }

    @GetMapping("/{testId}/edit")
    private String showEditTestPartForm(@PathVariable("testId") Long testId, Model model) {
        Optional<Test> optionalTest = testRepository.findById(testId);

        if (optionalTest.isPresent()) {
            model.addAttribute("test", optionalTest.get());
            model.addAttribute("subTestDto", subTestDTO.builder().subTest(optionalTest.get()).build());

            return "testPages/createTestPartForm";
        }

        return "redirect:/test/" + testId;
    }
    @GetMapping("/delete/{testId}")
    private String deleteTest(@PathVariable("testId") Long testId) {
        Optional<Test> optionalTest = testRepository.findById(testId);

        if (optionalTest.isPresent()) {
            testRepository.delete(optionalTest.get());
        }

        return "redirect:/test/all";
    }

    @PostMapping("/new")
    private String saveOrUpdateTest(@ModelAttribute("test") Test superTestToBeSaved, subTestDTO subTestDto, BindingResult result) {

        if (!result.hasErrors()) {
            subTestDto.setSuperTest(superTestToBeSaved);
            testRepository.save(superTestToBeSaved);
        }

        return "redirect:/test/new/contents/" + superTestToBeSaved.getAssignmentId();
    }

    @PostMapping("/new/contents")
    private String saveOrUpdateTestPart(
            @ModelAttribute("subTestDto") subTestDTO subTestDto, BindingResult result) {
            if (!result.hasErrors()) {
                Test subTest = subTestDto.getSubTest();
                subTest.setSuperTest(subTestDto.getSuperTest());
                testRepository.save(subTest);
            }

        return "redirect:/test/all";
    }

    @PostMapping(value = "/new/contents", params = "next")
    private String nextSectionForm(@ModelAttribute("test") subTestDTO test, BindingResult result) {
        if (!result.hasErrors()) {
            Test subTest = test.getSubTest();
            subTest.setSuperTest(test.getSuperTest());
            testRepository.save(subTest);
        }
        return "redirect:/test/new/contents/" + test.getSuperTest().getAssignmentId();
    }

    @PostMapping(value = "/new/contents", params = "subSection")
    private String nextLayerForm(@ModelAttribute("test") subTestDTO test, BindingResult result) {
        if (!result.hasErrors()) {
            Test subTest = test.getSubTest();
            subTest.setSuperTest(test.getSuperTest());
            testRepository.save(subTest);
        }
        return "redirect:/test/new/contents/" + test.getSubTest().getAssignmentId();
    }

    @PostMapping(value = "/new", params = "cancel")
    private String cancelForm() {

        return "redirect:/test/all";
    }
}
