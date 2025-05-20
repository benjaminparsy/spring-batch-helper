package com.benjamin.parsy.sbh.reader.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;

/**
 * Functional interface for fetching paginated REST responses.
 *
 * <p>Used in {@link RestItemReader} to retrieve each page of data sequentially by page number.</p>
 *
 * @param <R> the type of the response body
 */
@FunctionalInterface
public interface PagingFetcher<R> {

    /**
     * Fetches the data for the specified page number.
     *
     * @param page the page number to fetch, starting from 1
     * @return a {@link ResponseEntity} containing the response body
     */
    @NonNull
    ResponseEntity<R> doFetch(int page);

}
