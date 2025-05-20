package com.benjamin.parsy.sbh.reader.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;

/**
 * Functional interface for fetching a single REST response.
 *
 * <p>Used in {@link RestItemReader} when only one request is needed to retrieve all data.</p>
 *
 * @param <R> the type of the response body
 */
@FunctionalInterface
public interface SingleFetchFetcher<R> {

    /**
     * Fetches the data once.
     *
     * @return a {@link ResponseEntity} containing the response body
     */
    @NonNull
    ResponseEntity<R> doFetch();

}
