package com.example.bookportal.controller;

import com.example.bookportal.service.AuthorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    // AUTHOR SEARCH PAGE
    @GetMapping
    public String authorPage(Model model) {
        model.addAttribute("authors", authorService.getAllAuthors());
        return "author";
    }

    // AUTHOR SUMMARY PAGE
    @GetMapping("/{id}")
    public String authorSummary(@PathVariable Long id, Model model) {

        model.addAttribute("author", authorService.getAuthorById(id));
        model.addAttribute("categories",
                authorService.getCategoryWiseBooks(id));

        return "author-summary";
    }
}
