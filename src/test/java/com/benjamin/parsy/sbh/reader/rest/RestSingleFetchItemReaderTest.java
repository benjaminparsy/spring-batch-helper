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

class RestSingleFetchItemReaderTest {

    private static final ClassPathResource JSON_RESPONSE;
    private static final ObjectMapper OBJECT_MAPPER;
    private StepExecution stepExecution;

    static {
        JSON_RESPONSE = new ClassPathResource("rest-response/response-page-1.json");
        OBJECT_MAPPER = new ObjectMapper();
    }

    @BeforeEach
    void setUp() {
        stepExecution = MetaDataInstanceFactory.createStepExecution();
    }

    @Test
    void givenApiData_whenJobExecuted_thenReadOk() throws Exception {

        // Given
        RestSingleFetchItemReader<ApiResponseDto.DataResponseDto, ApiResponseDto> reader = createReader(fetcherOk(), responseExtractorOk());

        // When
        List<ApiResponseDto.DataResponseDto> dataResponseDtoList = ReaderUtils.readAll(stepExecution, reader);

        // Then
        assertEquals(6, dataResponseDtoList.size());

    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void givenResponseNull_whenJobExecuted_thenReadOk() throws Exception {

        // Given
        RestSingleFetchItemReader<ApiResponseDto.DataResponseDto, ApiResponseDto> reader = createReader(() -> null, responseExtractorOk());

        // When
        List<ApiResponseDto.DataResponseDto> dataResponseDtoList = ReaderUtils.readAll(stepExecution, reader);

        // Then
        assertEquals(0, dataResponseDtoList.size());

    }

    @Test
    void givenFetchError_whenJobExecuted_thenThrowException() {

        // Given
        RestSingleFetchItemReader<ApiResponseDto.DataResponseDto, ApiResponseDto> reader = createReader(fetcherKo(), responseExtractorOk());

        // When
        RestReaderException exception = assertThrows(
                RestReaderException.class,
                () -> ReaderUtils.readAll(stepExecution, reader));

        // Then
        assertEquals("Failed to fetch data", exception.getMessage());

    }

    @Test
    void givenExtractError_whenJobExecuted_thenThrowException() {

        // Given
        RestSingleFetchItemReader<ApiResponseDto.DataResponseDto, ApiResponseDto> reader = createReader(fetcherOk(), responseExtractorKo());

        // When
        RestReaderException exception = assertThrows(
                RestReaderException.class,
                () -> ReaderUtils.readAll(stepExecution, reader));

        // Then
        assertEquals("Failed to extract data from response", exception.getCause().getMessage());

    }

    private RestSingleFetchItemReader<ApiResponseDto.DataResponseDto, ApiResponseDto> createReader(
            SingleFetchFetcher<ApiResponseDto> fetcher,
            ResponseExtractor<ApiResponseDto.DataResponseDto, ApiResponseDto> extractor) {

        RestSingleFetchItemReader<ApiResponseDto.DataResponseDto, ApiResponseDto> reader = new RestSingleFetchItemReader<>();
        reader.setFetcher(fetcher);
        reader.setResponseExtractor(extractor);
        reader.afterPropertiesSet();

        return reader;
    }

    private SingleFetchFetcher<ApiResponseDto> fetcherOk() {
        return () -> ResponseEntity.ok(OBJECT_MAPPER.readValue(JSON_RESPONSE.getFile(), ApiResponseDto.class));
    }

    private SingleFetchFetcher<ApiResponseDto> fetcherKo() {
        return () -> {
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
