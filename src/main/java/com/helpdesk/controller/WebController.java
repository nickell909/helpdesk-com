package com.helpdesk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/tickets")
    public String tickets() {
        return "tickets";
    }

    @GetMapping("/tickets/new")
    public String newTicket() {
        return "ticket-new";
    }

    @GetMapping("/tickets/{id}")
    public String ticketDetail(@PathVariable Long id) {
        return "ticket-detail";
    }

    @GetMapping("/admin/users")
    public String users() {
        return "users";
    }
}
