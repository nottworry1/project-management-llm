package org.kupchenko.projectmanagementllm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class LoginController
{

    @GetMapping("/login")
    public String login()
    {
        return "authentication/login";
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/")
    public String index()
    {
        return "redirect:/projects";
    }
}

