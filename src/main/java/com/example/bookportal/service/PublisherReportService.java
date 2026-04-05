package com.example.bookportal.service;

import com.example.bookportal.entity.PublisherEntity;

import java.util.List;
import java.util.Locale;

/**
 * Service interface for publisher report generation.
 */
public interface PublisherReportService {
    record PublisherReportPageData(List<PublisherEntity> publishers, Long selectedPublisherId) {
    }

    /**
     * Builds the publisher report page data.
     *
     * @param selectedPublisherId optional selected publisher id
     * @return page data for the report screen
     */
    PublisherReportPageData buildPublisherReportPageData(Long selectedPublisherId);

    /**
     * Generates the publisher Jasper report as PDF.
     *
     * @param publisherId optional publisher ID filter, null means all publishers
     * @return generated PDF bytes
     */
    byte[] generatePublisherReportPdf(Long publisherId, Locale locale);

    /**
     * Resolves a meaningful PDF filename for the current report filter.
     *
     * @param publisherId optional publisher id filter
     * @return safe filename
     */
    String resolveReportFilename(Long publisherId);
}
