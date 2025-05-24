package com.benjamin.parsy.sbh.test;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.*;
import org.springframework.batch.test.MetaDataInstanceFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReaderUtilsTest {

    @Test
    void givenItemReader_whenReadAll_thenUtilityReturnAllValues() {

        // Given
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();

        List<String> values = new ArrayList<>();
        values.add("value1");
        values.add("value2");
        values.add("value3");

        ItemReader<String> itemReader = () -> values.isEmpty() ? null : values.remove(0);

        // When
        List<String> readValues = assertDoesNotThrow(() -> ReaderUtils.readAll(stepExecution, itemReader));

        // Then
        assertEquals(3, readValues.size());

    }

    @Test
    void givenItemStreamReader_whenReadAll_thenUtilityReturnAllValues() {

        // Given
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();

        List<String> values = new ArrayList<>();
        values.add("value1");
        values.add("value2");
        values.add("value3");

        ItemStreamReader<String> itemReader = new ItemStreamReader<String>() {
            @Override
            public String read() {
                return values.isEmpty() ? null : values.remove(0);
            }

            @Override
            public void open(ExecutionContext executionContext) throws ItemStreamException {
                executionContext.put("openExecuted", true);
            }

            @Override
            public void update(ExecutionContext executionContext) throws ItemStreamException {
                executionContext.put("updateExecuted", true);
            }

            @Override
            public void close() throws ItemStreamException {
                stepExecution.getExecutionContext().put("closeExecuted", true);
            }
        };

        // When
        List<String> readValues = assertDoesNotThrow(() -> ReaderUtils.readAll(stepExecution, itemReader));

        // Then
        assertEquals(3, readValues.size());
        assertEquals(Boolean.TRUE, stepExecution.getExecutionContext().get("openExecuted"));
        assertEquals(Boolean.TRUE, stepExecution.getExecutionContext().get("updateExecuted"));
        assertEquals(Boolean.TRUE, stepExecution.getExecutionContext().get("closeExecuted"));

    }

}