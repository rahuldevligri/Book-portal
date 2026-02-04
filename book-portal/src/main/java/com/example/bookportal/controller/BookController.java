package com.example.bookportal.controller;

import com.example.bookportal.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public String booksPage(
            @RequestParam Long authorId,
            @RequestParam Long categoryId,
            Model model) {

        model.addAttribute("books",
                bookService.getBooksByAuthorAndCategory(authorId, categoryId));

        return "books";
    }
}

