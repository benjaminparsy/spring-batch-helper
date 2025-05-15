package com.benjamin.parsy.sbh.reader.rest;

import com.benjamin.parsy.sbh.ReaderUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RestPagingItemReaderTest {

    private static final ClassPathResource JSON_RESPONSE_PAGE_1;
    private static final ClassPathResource JSON_RESPONSE_PAGE_2;
    private static final ObjectMapper OBJECT_MAPPER;
    private StepExecution stepExecution;

    static {
        JSON_RESPONSE_PAGE_1 = new ClassPathResource("rest-response/response-page-1.json");
        JSON_RESPONSE_PAGE_2 = new ClassPathResource("rest-response/response-page-2.json");
        OBJECT_MAPPER = new ObjectMapper();
    }

    @BeforeEach
    void setUp() {
        stepExecution = MetaDataInstanceFactory.createStepExecution();
    }

    @Test
    void givenApiData_whenJobExecuted_thenReadOk() throws Exception {

        // Given
        RestPagingItemReader<ApiResponseDto.DataResponseDto, ApiResponseDto> reader = createReader(fetcherOk(), responseExtractorOk());

        // When
        List<ApiResponseDto.DataResponseDto> dataResponseDtoList = ReaderUtils.readAll(stepExecution, reader);

        // Then
        assertEquals(12, dataResponseDtoList.size());

    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void givenResponseNull_whenJobExecuted_thenReadOk() throws Exception {

        // Given
        RestPagingItemReader<ApiResponseDto.DataResponseDto, ApiResponseDto> reader = createReader(page -> null, responseExtractorOk());

        // When
        List<ApiResponseDto.DataResponseDto> dataResponseDtoList = ReaderUtils.readAll(stepExecution, reader);

        // Then
        assertEquals(0, dataResponseDtoList.size());

    }

    @Test
    void givenFetchError_whenJobExecuted_thenThrowException() {

        // Given
        RestPagingItemReader<ApiResponseDto.DataResponseDto, ApiResponseDto> reader = createReader(fetcherKo(), responseExtractorOk());

        // When
        RestReaderException exception = assertThrows(
                RestReaderException.class,
                () -> ReaderUtils.readAll(stepExecution, reader));

        // Then
        assertEquals("Failed to fetch page 1", exception.getMessage());

    }

    @Test
    void givenExtractError_whenJobExecuted_thenThrowException() {

        // Given
        RestPagingItemReader<ApiResponseDto.DataResponseDto, ApiResponseDto> reader = createReader(fetcherOk(), responseExtractorKo());

        // When
        RestReaderException exception = assertThrows(
                RestReaderException.class,
                () -> ReaderUtils.readAll(stepExecution, reader));

        // Then
        assertEquals("Failed to extract data from response", exception.getCause().getMessage());

    }

    private RestPagingItemReader<ApiResponseDto.DataResponseDto, ApiResponseDto> createReader(
            PagingFetcher<ApiResponseDto> fetcher,
            ResponseExtractor<ApiResponseDto.DataResponseDto, ApiResponseDto> extractor) {

        RestPagingItemReader<ApiResponseDto.DataResponseDto, ApiResponseDto> reader = new RestPagingItemReader<>();
        reader.setFetcher(fetcher);
        reader.setResponseExtractor(extractor);
        reader.afterPropertiesSet();

        return reader;
    }

    private PagingFetcher<ApiResponseDto> fetcherOk() {
        return page -> {
            ApiResponseDto dto;
            switch (page) {
                case 1:
                    dto = OBJECT_MAPPER.readValue(JSON_RESPONSE_PAGE_1.getFile(), ApiResponseDto.class);
                    break;
                case 2:
                    dto = OBJECT_MAPPER.readValue(JSON_RESPONSE_PAGE_2.getFile(), ApiResponseDto.class);
                    break;
                default:
                    dto = null;
                    break;
            }
            return ResponseEntity.ok(dto);
        };
    }

    private PagingFetcher<ApiResponseDto> fetcherKo() {
        return page -> {
            throw new RestClientException("The request is incorrect");
        };
    }

    private ResponseExtractor<ApiResponseDto.DataResponseDto, ApiResponseDto> responseExtractorOk() {
        return response -> response.getBody() == null ? null : response.getBody().getData();
    }

    private ResponseExtractor<ApiResponseDto.DataResponseDto, ApiResponseDto> responseExtractorKo() {
        return response -> {
            throw new NullPointerException();
        };
    }

}
