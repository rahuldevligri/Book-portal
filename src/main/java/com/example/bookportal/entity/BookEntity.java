package com.example.bookportal.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Locale;
import java.util.regex.Pattern;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "book")
public class BookEntity extends BaseEntity {
    private static final Pattern SPLIT_PATTERN = Pattern.compile("\\s+");
    private static final String GENERIC_BOOK_THUMBNAIL = "/thumb/genericBook.jpg";

    @Column(name = "TITLE")
    private String title;

    @Column(name = "BOOK_CATEGORY_ID")
    private Long bookCategoryId;

    @Column(name = "THUMBNAIL_PATH")
    private String thumbnailPath;

    @Column(name = "IMAGE_PATH")
    private String imagePath;

    @Transient
    public String getThumbnailUrl() {
        String thumb = normalizeAssetPath(thumbnailPath, "/thumb/");
        if (thumb != null) {
            return thumb;
        }
        String full = normalizeAssetPath(imagePath, "/images/");
        return full != null ? full : GENERIC_BOOK_THUMBNAIL;
    }

    @Transient
    public String getFullImageUrl() {
        String full = normalizeAssetPath(imagePath, "/images/");
        if (full != null) {
            return full;
        }
        String thumb = normalizeAssetPath(thumbnailPath, "/thumb/");
        return thumb != null ? thumb : GENERIC_BOOK_THUMBNAIL;
    }

    private String normalizeAssetPath(String rawPath, String fallbackPrefix) {
        if (rawPath == null || rawPath.isBlank()) {
            return null;
        }

        String best = pickBestToken(rawPath);
        if (best == null || best.isBlank()) {
            return null;
        }

        String normalized = best.replace("\\", "/").trim();
        String lower = normalized.toLowerCase(Locale.ROOT);
        if (lower.startsWith("http://") || lower.startsWith("https://") || lower.startsWith("data:")) {
            return normalized;
        }
        if (normalized.startsWith("/")) {
            return normalized;
        }
        if (normalized.contains("/")) {
            return "/" + normalized;
        }

        String prefix = (fallbackPrefix == null || fallbackPrefix.isBlank()) ? "/images/" : fallbackPrefix;
        if (!prefix.startsWith("/")) {
            prefix = "/" + prefix;
        }
        if (!prefix.endsWith("/")) {
            prefix = prefix + "/";
        }
        return prefix + normalized;
    }

    private String pickBestToken(String rawPath) {
        String normalized = rawPath.replace("\\", "/").trim();
        if (!normalized.contains(" ")) {
            return stripToken(normalized);
        }

        String[] parts = SPLIT_PATTERN.split(normalized);
        String best = null;
        int bestScore = Integer.MIN_VALUE;
        for (String part : parts) {
            String token = stripToken(part);
            if (token == null || token.isBlank()) {
                continue;
            }
            int score = 0;
            String lower = token.toLowerCase(Locale.ROOT);
            if (lower.contains("/thumb/")) {
                score += 2;
            }
            if (lower.contains("/images/")) {
                score += 2;
            }
            if (token.startsWith("/")) {
                score += 1;
            }
            if (lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png")
                    || lower.endsWith(".gif") || lower.endsWith(".webp")) {
                score += 1;
            }
            if (score > bestScore) {
                bestScore = score;
                best = token;
            }
        }
        return best != null ? best : stripToken(normalized);
    }

    private String stripToken(String token) {
        if (token == null) {
            return null;
        }
        return token.replaceAll("^[\"'`]+|[\"'`,;]+$", "").trim();
    }
}
