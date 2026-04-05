package com.example.bookportal.repository.projection;

/**
 * Projection used for fast search result retrieval via native queries.
 *
 * Note: getter names must match the column aliases in the native SQL.
 */
public interface SearchBookSummaryProjection {
    Long getId();

    String getTitle();

    String getAuthorName();

    String getCategoryName();

    String getPublisherName();

    String getImagePath();

    String getThumbnailPath();
}
