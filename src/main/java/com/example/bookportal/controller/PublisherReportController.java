package com.example.bookportal.controller;

import com.example.bookportal.exception.ValidationException;
import com.example.bookportal.service.PublisherReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Locale;

/**
 * Controller for publisher report UI and PDF generation.
 */
@Controller
@RequestMapping("/reports/publishers")
public class PublisherReportController {

    @Autowired
    private PublisherReportService publisherReportService;

    /**
     * Displays publisher report filter screen.
     *
     * @param model page model
     * @return filter page
     */
    @GetMapping
    public String publisherReportPage(@RequestParam(required = false) Long publisherId, Model model) {
        PublisherReportService.PublisherReportPageData pageData =
                publisherReportService.buildPublisherReportPageData(publisherId);
        model.addAttribute("publishers", pageData.publishers());
        model.addAttribute("selectedPublisherId", pageData.selectedPublisherId());
        return "publisher-report";
    }

    /**
     * Generates publisher report with optional publisher filter.
     *
     * @param publisherId optional publisher ID
     * @return PDF response
     */
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> publisherReportPdf(@RequestParam(required = false) Long publisherId,
                                                     @RequestParam(required = false) String lang) {
        try {
            Locale locale = (lang == null || lang.isBlank())
                    ? LocaleContextHolder.getLocale()
                    : Locale.forLanguageTag(lang);
            byte[] pdf = publisherReportService.generatePublisherReportPdf(publisherId, locale);

            var headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.inline()
                    .filename(publisherReportService.resolveReportFilename(publisherId))
                    .build());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdf);
        } catch (ValidationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }
}
