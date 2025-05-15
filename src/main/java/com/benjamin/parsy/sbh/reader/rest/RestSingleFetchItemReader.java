package com.benjamin.parsy.sbh.reader.rest;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;

@Slf4j
public class RestSingleFetchItemReader<I, R> extends AbstractRestItemReader<I> implements InitializingBean {

    @Setter private SingleFetchFetcher<R> fetcher;
    @Setter private ResponseExtractor<I, R> responseExtractor;
    private boolean alreadyFetched;

    public RestSingleFetchItemReader() {
        this.alreadyFetched = false;
    }

    @Override
    protected List<I> doRead() {

        if (alreadyFetched) {
            return Collections.emptyList();
        }

        alreadyFetched = true;

        try {
            ResponseEntity<R> response = fetcher.doFetch();
            return extractData(response);
        } catch (Exception e) {
            throw new RestReaderException("Failed to fetch data", e);
        }

    }

    private List<I> extractData(ResponseEntity<R> response) {

        if (response == null) {
            return Collections.emptyList();
        }

        try {
            return responseExtractor.doExtract(response);
        } catch (Exception e) {
            throw new RestReaderException("Failed to extract data from response", e);
        }

    }

    public void afterPropertiesSet() {
        Assert.notNull(this.fetcher, "fetcher is required");
        Assert.notNull(this.responseExtractor, "responseExtractor is required");
    }

}
