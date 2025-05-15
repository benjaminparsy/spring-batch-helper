package com.benjamin.parsy.sbh.reader.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;

import java.io.IOException;

@FunctionalInterface
public interface PagingFetcher<R> {

    @NonNull
    ResponseEntity<R> doFetch(int page) throws IOException;

}
