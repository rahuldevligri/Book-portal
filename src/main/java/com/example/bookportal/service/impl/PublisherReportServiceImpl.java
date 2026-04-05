package com.example.bookportal.service.impl;

import com.example.bookportal.entity.PublisherEntity;
import com.example.bookportal.exception.ValidationException;
import com.example.bookportal.repository.PublisherRepository;
import com.example.bookportal.service.PublisherReportService;
import jakarta.annotation.PostConstruct;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Jasper-powered publisher report service implementation.
 */
@Service
public class PublisherReportServiceImpl implements PublisherReportService {

    private static final String REPORT_PATH = "reports/publisher_report.jrxml";
    private static final String IMAGE_PATH_AR = "reports/flower2.png";
    private static final String IMAGE_PATH_DEFAULT = "reports/flower1.png";

    @Autowired
    private DataSource dataSource;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private PublisherRepository publisherRepository;

    // Cache compiled report in memory
    private JasperReport compiledReport;

    /**
     * Compiles the JasperReport strictly once when the Spring Boot context initializes.
     * This saves massive CPU/Memory overhead on every PDF request.
     */
    @PostConstruct
    public void init() {
        try (var reportStream = new ClassPathResource(REPORT_PATH).getInputStream()) {
            this.compiledReport = JasperCompileManager.compileReport(reportStream);
        } catch (IOException | JRException e) {
            throw new IllegalStateException("Failed to compile JasperReport template at startup", e);
        }
    }

    @Override
    public PublisherReportPageData buildPublisherReportPageData(Long selectedPublisherId) {
        Long safeSelectedPublisherId = normalizePublisherId(selectedPublisherId);

        List<PublisherEntity> publishers = publisherRepository.findAll().stream()
                .sorted(Comparator.comparing(PublisherEntity::getName,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();
        return new PublisherReportPageData(publishers, safeSelectedPublisherId);
    }

    @Override
    public byte[] generatePublisherReportPdf(Long publisherId, Locale locale) {
        // Try-with-resources ensures the DB connection is released immediately
        try (var connection = dataSource.getConnection()) {
            Long safePublisherId = normalizePublisherId(publisherId);

            Locale effectiveLocale = (locale == null) ? LocaleContextHolder.getLocale() : locale;
            boolean isArabic = "ar".equalsIgnoreCase(effectiveLocale.getLanguage());

            var parameters = new HashMap<String, Object>();
            parameters.put("p_publisher_id", safePublisherId);

            var imagePath = isArabic ? IMAGE_PATH_AR : IMAGE_PATH_DEFAULT;
            parameters.put("p_background_image", new ClassPathResource(imagePath).getURL().toString());

            parameters.put("p_report_title", messageSource.getMessage("pdf.publisher.report.title", null, effectiveLocale));
            parameters.put("p_report_subtitle", messageSource.getMessage("pdf.publisher.report.subtitle", null, effectiveLocale));
            parameters.put("p_label_distributor_id", messageSource.getMessage("pdf.publisher.report.distributor.id", null, effectiveLocale));
            parameters.put("p_label_distributor_name", messageSource.getMessage("pdf.publisher.report.distributor.name", null, effectiveLocale));
            parameters.put("p_label_telephone", messageSource.getMessage("pdf.publisher.report.telephone", null, effectiveLocale));
            parameters.put("p_label_email", messageSource.getMessage("pdf.publisher.report.email", null, effectiveLocale));
            parameters.put("p_label_book_id", messageSource.getMessage("pdf.publisher.report.book.id", null, effectiveLocale));
            parameters.put("p_label_book_title", messageSource.getMessage("pdf.publisher.report.book.title", null, effectiveLocale));
            parameters.put("p_label_page", messageSource.getMessage("pdf.publisher.report.page", null, effectiveLocale));
            parameters.put("p_label_of", messageSource.getMessage("pdf.publisher.report.of", null, effectiveLocale));
            parameters.put("p_is_rtl", isArabic);
            parameters.put(JRParameter.REPORT_LOCALE, effectiveLocale);

            var jasperPrint = JasperFillManager.fillReport(compiledReport, parameters, connection);
            return JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (Exception ex) {
            throw new IllegalStateException("Failed to generate publisher report PDF", ex);
        }
    }

    @Override
    public String resolveReportFilename(Long publisherId) {
        Long safePublisherId = normalizePublisherId(publisherId);
        if (safePublisherId == null) {
            return "publisher_report_all.pdf";
        }
        return "publisher_report_" + safePublisherId + ".pdf";
    }

    private Long normalizePublisherId(Long publisherId) {
        if (publisherId == null) {
            return null;
        }
        if (publisherId <= 0) {
            throw new ValidationException(messageSource.getMessage("publisher.report.invalid", null,
                    LocaleContextHolder.getLocale()));
        }
        return publisherId;
    }
}
