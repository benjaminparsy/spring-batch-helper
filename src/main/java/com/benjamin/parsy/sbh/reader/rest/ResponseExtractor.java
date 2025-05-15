package com.benjamin.parsy.sbh.reader.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;

import java.util.List;

@FunctionalInterface
public interface ResponseExtractor<I, R> {

    @Nullable
    List<I> doExtract(ResponseEntity<R> response);

}
