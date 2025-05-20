package com.benjamin.parsy.sbh.reader.rest;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@Slf4j
public class RestItemReader<I, R> extends AbstractRestItemReader<I> implements InitializingBean {

    @Setter private FetchMode mode = FetchMode.SINGLE;
    @Setter private SingleFetchFetcher<R> singleFetcher;
    @Setter private PagingFetcher<R> pagingFetcher;
    @Setter private ResponseExtractor<I, R> responseExtractor;
    @Setter private int currentPage = 1;

    private boolean alreadyFetched;
    private boolean finished;

    public RestItemReader() {
        this.alreadyFetched = false;
        this.finished = false;
    }

    @Override
    protected List<I> doRead() {

        if (mode == FetchMode.SINGLE) {
            if (alreadyFetched) return Collections.emptyList();
            alreadyFetched = true;
            return fetchAndExtract(() -> singleFetcher.doFetch());
        }

        if (finished) return Collections.emptyList();

        return fetchAndExtract(() -> pagingFetcher.doFetch(currentPage));
    }


    private List<I> fetchAndExtract(Supplier<ResponseEntity<R>> fetcher) {

        try {
            ResponseEntity<R> response = fetcher.get();
            return extractData(response);
        } catch (Exception e) {
            String err = mode == FetchMode.SINGLE
                    ? "Failed to fetch data"
                    : "Failed to fetch page " + currentPage;
            throw new RestReaderException(err, e);
        }
    }

    private List<I> extractData(ResponseEntity<R> response) {

        if (response == null) return Collections.emptyList();

        try {
            List<I> items = responseExtractor.doExtract(response);

            if (mode == FetchMode.PAGING) {
                if (items == null || items.isEmpty()) {
                    finished = true;
                    return Collections.emptyList();
                }
                currentPage++;
            }

            return items;

        } catch (Exception e) {
            throw new RestReaderException("Failed to extract data from response", e);
        }
    }

    @Override
    public void afterPropertiesSet() {

        Assert.notNull(this.responseExtractor, "responseExtractor is required");
        Assert.notNull(this.mode, "mode is required");

        if (mode == FetchMode.SINGLE) {
            Assert.notNull(this.singleFetcher, "singleFetcher is required in SINGLE mode");
        } else {
            Assert.notNull(this.pagingFetcher, "pagingFetcher is required in PAGING mode");
            Assert.isTrue(this.currentPage > 0, "currentPage must be greater than zero");
        }

    }
}
