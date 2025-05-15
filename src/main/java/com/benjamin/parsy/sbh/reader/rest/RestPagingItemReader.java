package com.benjamin.parsy.sbh.reader.rest;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;

@Slf4j
public class RestPagingItemReader<I, R> extends AbstractRestItemReader<I> implements InitializingBean {

    @Setter private PagingFetcher<R> fetcher;
    @Setter private ResponseExtractor<I, R> responseExtractor;
    @Setter private int currentPage;
    private boolean finished;

    public RestPagingItemReader() {
        this.currentPage = 1;
        this.finished = false;
    }

    @Override
    protected List<I> doRead() {

        if (finished) return Collections.emptyList();

        try {

            ResponseEntity<R> response = fetcher.doFetch(currentPage);
            return extractData(response);

        } catch (Exception e) {
            throw new RestReaderException("Failed to fetch page " + currentPage, e);
        }

    }

    private List<I> extractData(ResponseEntity<R> response) {

        if (response == null) {
            return Collections.emptyList();
        }

        try {
            List<I> items = responseExtractor.doExtract(response);

            if (items == null || items.isEmpty()) {
                finished = true;
                return Collections.emptyList();
            }

            currentPage++;

            return items;
        } catch (Exception e) {
            throw new RestReaderException("Failed to extract data from response", e);
        }

    }

    public void afterPropertiesSet() {
        Assert.notNull(this.fetcher, "fetcher is required");
        Assert.notNull(this.responseExtractor, "responseExtractor is required");
        Assert.isTrue(this.currentPage > 0, "currentPage must be greater than zero");
    }

}
