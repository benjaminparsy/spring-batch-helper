package com.benjamin.parsy.sbh.reader.rest;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Generic Spring Batch ItemReader for consuming REST APIs, supporting both
 * single-fetch and paginated-fetch modes.
 *
 * <p>This reader is designed for flexibility, allowing the user to specify
 * whether to fetch all data at once (SINGLE mode), or fetch it page by page
 * (PAGING mode). The appropriate fetcher must be provided depending on the selected mode.</p>
 *
 * <p>The reader delegates the transformation of the API response into items via
 * a {@link ResponseExtractor}, allowing custom conversion logic.</p>
 *
 * <p>Supported fetch modes:</p>
 * <ul>
 *     <li>{@link FetchMode#SINGLE} — data is fetched once using a {@link SingleFetchFetcher}</li>
 *     <li>{@link FetchMode#PAGING} — data is fetched over multiple pages using a {@link PagingFetcher}</li>
 * </ul>
 *
 * <p>Example (paging):</p>
 * <pre>{@code
 * RestItemReader<MyItem, MyApiResponse> reader = new RestItemReader<>();
 * reader.setMode(FetchMode.PAGING);
 * reader.setPagingFetcher(page -> restTemplate.getForEntity(url + "?page=" + page, MyApiResponse.class));
 * reader.setResponseExtractor(response -> response.getBody().getItems());
 * reader.afterPropertiesSet();
 * }</pre>
 *
 * @param <I> the type of items read
 * @param <R> the type of the API response body
 */
@Slf4j
public class RestItemReader<I, R> extends AbstractRestItemReader<I> implements InitializingBean {

    /**
     * Defines the fetch mode used by this reader.
     * Must be set before initialization.
     *
     * @see FetchMode
     */
    @Setter private FetchMode mode;

    /**
     * Fetcher for SINGLE mode.
     * Used only if {@link #mode} is set to {@link FetchMode#SINGLE}.
     */
    @Setter private SingleFetchFetcher<R> singleFetcher;

    /**
     * Fetcher for PAGING mode.
     * Used only if {@link #mode} is set to {@link FetchMode#PAGING}.
     */
    @Setter private PagingFetcher<R> pagingFetcher;

    /**
     * Extractor responsible for transforming the raw REST response into a list of items.
     * Must be set in all modes.
     */
    @Setter private ResponseExtractor<I, R> responseExtractor;

    /**
     * The current page to fetch.
     * Only relevant in PAGING mode. Defaults to 1.
     */
    @Setter private int currentPage = 1;

    /**
     * Indicates whether the reader has already fetched data in SINGLE mode.
     */
    private boolean alreadyFetched;

    /**
     * Indicates whether all data has been read in PAGING mode.
     */
    private boolean finished;

    public RestItemReader() {
        this.alreadyFetched = false;
        this.finished = false;
    }

    /**
     * Reads a chunk of items from the REST API based on the configured mode.
     *
     * <p>If mode is SINGLE, data will be fetched once, and then return empty on subsequent calls.</p>
     * <p>If mode is PAGING, data will be fetched page by page until an empty page is returned.</p>
     *
     * @return a list of items, or an empty list if no more data
     * @throws RestReaderException if an error occurs while fetching or extracting data
     */
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

    /**
     * Fetches data using the provided fetcher and immediately extracts it using the response extractor.
     *
     * @param fetcher a supplier that provides the {@link ResponseEntity} to extract data from
     * @return a list of extracted items, or an empty list if the response is null
     * @throws RestReaderException if the fetch or extraction process fails
     */
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

    /**
     * Applies the {@link ResponseExtractor} to the given response to extract the list of items.
     *
     * @param response the REST response to extract items from
     * @return the extracted list of items, or an empty list if the response is null
     * @throws RestReaderException if extraction fails
     */
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

    /**
     * Validates the configuration of the reader based on the selected mode.
     *
     * @throws IllegalArgumentException if any required property is missing
     */
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
