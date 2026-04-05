package com.example.bookportal.specification;

import com.example.bookportal.entity.BookEntity;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    /**
     * Returns a specification that always evaluates to true (active books).
     *
     * @return a specification for active books
     */
    public static Specification<BookEntity> active() {
        return (root, query, cb) -> cb.conjunction();
    }

    /**
     * Returns a specification for searching books by text, type, and match type.
     *
     * @param q         the query text
     * @param type      the field type to search (AUTHOR, PUBLISHER, CATEGORY,
     *                  TITLE, ALL)
     * @param matchType the match type (exact, start, contains)
     * @return a specification for searching books
     */
    public static Specification<BookEntity> containsText(String q, String type, String matchType) {
        String text = q == null ? "" : q.trim().toLowerCase();
        if (text.isEmpty()) {
            return (root, query, cb) -> cb.conjunction();
        }
        String like;
        if ("exact".equalsIgnoreCase(matchType)) {
            like = text;
        } else if ("start".equalsIgnoreCase(matchType)) {
            like = text + "%";
        } else {
            like = "%" + text + "%";
        }
        String t = type == null ? "ALL" : type.trim().toUpperCase();
        switch (t) {
            case "AUTHOR":
                return (root, query, cb) -> {
                    var subquery = query.subquery(Long.class);
                    var bookAuthor = subquery.from(com.example.bookportal.entity.BookAuthorEntity.class);
                    var author = subquery.from(com.example.bookportal.entity.AuthorEntity.class);

                    var firstName = cb.lower(author.get("firstName"));
                    var lastName = cb.lower(author.get("lastName"));
                    // Allow matching across first/last name boundary (e.g. query "ns" should match
                    // "N" + "Singh")
                    var fullNoSpace = cb.concat(firstName, lastName);
                    var fullWithSpace = cb.concat(cb.concat(firstName, cb.literal(" ")), lastName);

                    boolean exact = "exact".equalsIgnoreCase(matchType);

                    // For exact matches: require full-name equality (first+last) OR single-name
                    // equality only when last name is empty/null.
                    // For non-exact matches: allow partial matches against first, last, or combined
                    // forms.
                    var matchPredicate = exact
                            ? cb.or(
                                    // single-name exact when author has no last name
                                    cb.and(
                                            cb.or(cb.isNull(author.get("lastName")),
                                                    cb.equal(author.get("lastName"), "")),
                                            cb.equal(firstName, like)),
                                    cb.equal(fullNoSpace, like),
                                    cb.equal(fullWithSpace, like))
                            : cb.or(
                                    cb.like(firstName, like),
                                    cb.like(lastName, like),
                                    cb.like(fullNoSpace, like),
                                    cb.like(fullWithSpace, like));

                    subquery.select(bookAuthor.get("bookId"))
                            .where(
                                    cb.and(
                                            cb.equal(bookAuthor.get("bookId"), root.get("id")),
                                            cb.equal(author.get("id"), bookAuthor.get("authorId")),
                                            matchPredicate));
                    return cb.in(root.get("id")).value(subquery);
                };
            case "PUBLISHER":
                return (root, query, cb) -> {
                    var subquery = query.subquery(Long.class);
                    var bookPublisher = subquery.from(com.example.bookportal.entity.BookPublisherEntity.class);
                    var publisher = subquery.from(com.example.bookportal.entity.PublisherEntity.class);
                    subquery.select(bookPublisher.get("bookId"))
                            .where(
                                    cb.and(
                                            cb.equal(bookPublisher.get("bookId"), root.get("id")),
                                            cb.equal(publisher.get("id"), bookPublisher.get("publisherId")),
                                            "exact".equalsIgnoreCase(matchType)
                                                    ? cb.equal(cb.lower(publisher.get("name")), like)
                                                    : cb.like(cb.lower(publisher.get("name")), like)));
                    return cb.in(root.get("id")).value(subquery);
                };
            case "CATEGORY":
                return (root, query, cb) -> {
                    var subquery = query.subquery(Long.class);
                    var category = subquery.from(com.example.bookportal.entity.CategoryEntity.class);
                    subquery.select(category.get("id"))
                            .where(
                                    cb.and(
                                            cb.equal(category.get("id"), root.get("bookCategoryId")),
                                            "exact".equalsIgnoreCase(matchType)
                                                    ? cb.equal(cb.lower(category.get("category")), like)
                                                    : cb.like(cb.lower(category.get("category")), like)));
                    return cb.in(root.get("bookCategoryId")).value(subquery);
                };
            case "TITLE":
            case "ALL":
            default:
                return (root, query, cb) -> "exact".equalsIgnoreCase(matchType)
                        ? cb.equal(cb.lower(root.get("title")), like)
                        : cb.like(cb.lower(root.get("title")), like);
        }
    }
}