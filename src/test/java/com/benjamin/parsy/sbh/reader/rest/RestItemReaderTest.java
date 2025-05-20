package com.benjamin.parsy.sbh.reader.rest;

import com.benjamin.parsy.sbh.test.ReaderUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RestItemReaderTest {

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
    void givenSingleMode_whenJobExecuted_thenReadOk() throws Exception {

        // Given
        RestItemReader<ApiResponseDto.DataResponseDto, ApiResponseDto> reader = new RestItemReaderBuilder<ApiResponseDto.DataResponseDto, ApiResponseDto>()
                .singleMode(singleFetchFetcherOk())
                .responseExtractor(responseExtractorOk())
                .build();

        // When
        List<ApiResponseDto.DataResponseDto> dataResponseDtoList = ReaderUtils.readAll(stepExecution, reader);

        // Then
        assertEquals(6, dataResponseDtoList.size());

    }

    @Test
    void givenPagingMode_whenJobExecuted_thenReadOk() throws Exception {

        // Given
        RestItemReader<ApiResponseDto.DataResponseDto, ApiResponseDto> reader = new RestItemReaderBuilder<ApiResponseDto.DataResponseDto, ApiResponseDto>()
                .pagingMode(pagingFetcherOk())
                .responseExtractor(responseExtractorOk())
                .build();

        // When
        List<ApiResponseDto.DataResponseDto> dataResponseDtoList = ReaderUtils.readAll(stepExecution, reader);

        // Then
        assertEquals(12, dataResponseDtoList.size());

    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void givenResponseNull_whenJobExecuted_thenReadOk() throws Exception {

        // Given
        RestItemReader<ApiResponseDto.DataResponseDto, ApiResponseDto> reader = new RestItemReaderBuilder<ApiResponseDto.DataResponseDto, ApiResponseDto>()
                .pagingMode(page -> null)
                .responseExtractor(responseExtractorOk())
                .build();

        // When
        List<ApiResponseDto.DataResponseDto> dataResponseDtoList = ReaderUtils.readAll(stepExecution, reader);

        // Then
        assertEquals(0, dataResponseDtoList.size());

    }

    @Test
    void givenFetchError_whenJobExecuted_thenThrowException() {

        // Given
        RestItemReader<ApiResponseDto.DataResponseDto, ApiResponseDto> reader = new RestItemReaderBuilder<ApiResponseDto.DataResponseDto, ApiResponseDto>()
                .pagingMode(page -> {
                    throw new RestClientException("The request is incorrect");
                })
                .responseExtractor(responseExtractorOk())
                .build();

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
        RestItemReader<ApiResponseDto.DataResponseDto, ApiResponseDto> reader = new RestItemReaderBuilder<ApiResponseDto.DataResponseDto, ApiResponseDto>()
                .pagingMode(pagingFetcherOk())
                .responseExtractor(response -> {
                    throw new NullPointerException();
                })
                .build();

        // When
        RestReaderException exception = assertThrows(
                RestReaderException.class,
                () -> ReaderUtils.readAll(stepExecution, reader));

        // Then
        assertEquals("Failed to extract data from response", exception.getCause().getMessage());

    }

    private SingleFetchFetcher<ApiResponseDto> singleFetchFetcherOk() {
        return () -> {
            try {
                ApiResponseDto dto = OBJECT_MAPPER.readValue(JSON_RESPONSE_PAGE_1.getFile(), ApiResponseDto.class);
                return ResponseEntity.ok(dto);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private PagingFetcher<ApiResponseDto> pagingFetcherOk() {
        return page -> {
            try {
                ApiResponseDto dto = null;
                if (page == 1) dto = OBJECT_MAPPER.readValue(JSON_RESPONSE_PAGE_1.getFile(), ApiResponseDto.class);
                if (page == 2) dto = OBJECT_MAPPER.readValue(JSON_RESPONSE_PAGE_2.getFile(), ApiResponseDto.class);
                return ResponseEntity.ok(dto);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private ResponseExtractor<ApiResponseDto.DataResponseDto, ApiResponseDto> responseExtractorOk() {
        return response -> response.getBody() == null ? null : response.getBody().getData();
    }

}