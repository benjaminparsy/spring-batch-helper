package com.benjamin.parsy.sbh.reader.rest;

public class RestItemReaderBuilder<I, R> {

    private final RestItemReader<I, R> reader = new RestItemReader<>();

    /**
     * Configures the reader to use single fetch mode with the given fetcher.
     *
     * @param fetcher the fetcher used to retrieve the single response
     * @return this builder instance
     */
    public RestItemReaderBuilder<I, R> singleMode(SingleFetchFetcher<R> fetcher) {
        reader.setMode(FetchMode.SINGLE);
        reader.setSingleFetcher(fetcher);
        return this;
    }

    /**
     * Configures the reader to use paging fetch mode with the given fetcher.
     *
     * @param fetcher the fetcher used to retrieve paginated responses
     * @return this builder instance
     */
    public RestItemReaderBuilder<I, R> pagingMode(PagingFetcher<R> fetcher) {
        reader.setMode(FetchMode.PAGING);
        reader.setPagingFetcher(fetcher);
        return this;
    }

    /**
     * Sets the response extractor to convert the raw API response to items.
     *
     * @param extractor the response extractor
     * @return this builder instance
     */
    public RestItemReaderBuilder<I, R> responseExtractor(ResponseExtractor<I, R> extractor) {
        reader.setResponseExtractor(extractor);
        return this;
    }

    /**
     * Builds the {@link RestItemReader} instance and validates its configuration.
     *
     * @return the configured reader
     * @throws IllegalStateException if required properties are missing
     */
    public RestItemReader<I, R> build() {
        try {
            reader.afterPropertiesSet();
        } catch (Exception e) {
            throw new IllegalStateException("Reader not properly configured", e);
        }
        return reader;
    }
}
