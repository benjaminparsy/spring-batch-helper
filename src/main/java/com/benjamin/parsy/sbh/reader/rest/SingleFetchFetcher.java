package com.benjamin.parsy.sbh.reader.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;

@FunctionalInterface
public interface SingleFetchFetcher<R> {

    @NonNull
    ResponseEntity<R> doFetch();

}
