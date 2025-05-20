package com.benjamin.parsy.sbh.reader.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * Functional interface for extracting a list of items from a REST response.
 *
 * <p>Used in conjunction with {@link RestItemReader} implementations to transform
 * a {@link ResponseEntity} into a list of domain objects.</p>
 *
 * @param <I> the type of item to be extracted
 * @param <R> the type of the raw response body
 */
@FunctionalInterface
public interface ResponseExtractor<I, R> {

    /**
     * Extracts a list of items from the given REST response.
     *
     * @param response the HTTP response returned by the fetcher
     * @return a list of extracted items, or {@code null} if no items could be extracted
     */
    @Nullable
    List<I> doExtract(ResponseEntity<R> response);

}
