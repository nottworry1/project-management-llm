package org.kupchenko.projectmanagementllm.controller;
import lombok.RequiredArgsConstructor;
import org.kupchenko.projectmanagementllm.dto.RegistrationForm;
import org.kupchenko.projectmanagementllm.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        return fillModelAndReturnPage(model);
    }

    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("form") RegistrationForm form,
                                      BindingResult bindingResult,
                                      Model model) {
        if (bindingResult.hasErrors()) {
            return fillModelAndReturnPage(model);
        }

        if (userService.emailExists(form.getEmail())) {
            model.addAttribute("emailError", "Email already taken");
            return fillModelAndReturnPage(model);
        }

        userService.registerUser(form);
        return "redirect:/login";
    }

    private static String fillModelAndReturnPage(Model model) {
        model.addAttribute("form", new RegistrationForm());
        return "authentication/register";
    }
}

