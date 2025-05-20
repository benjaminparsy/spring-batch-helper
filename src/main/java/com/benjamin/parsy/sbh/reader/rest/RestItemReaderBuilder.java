package com.benjamin.parsy.sbh.reader.rest;

public class RestItemReaderBuilder<I, R> {

    private final RestItemReader<I, R> reader = new RestItemReader<>();

    public RestItemReaderBuilder<I, R> singleMode(SingleFetchFetcher<R> fetcher) {
        reader.setMode(FetchMode.SINGLE);
        reader.setSingleFetcher(fetcher);
        return this;
    }

    public RestItemReaderBuilder<I, R> pagingMode(PagingFetcher<R> fetcher) {
        reader.setMode(FetchMode.PAGING);
        reader.setPagingFetcher(fetcher);
        return this;
    }

    public RestItemReaderBuilder<I, R> responseExtractor(ResponseExtractor<I, R> extractor) {
        reader.setResponseExtractor(extractor);
        return this;
    }

    public RestItemReader<I, R> build() {
        try {
            reader.afterPropertiesSet();
        } catch (Exception e) {
            throw new IllegalStateException("Reader not properly configured", e);
        }
        return reader;
    }
}
