package com.benjamin.parsy.sbh.test;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.test.StepScopeTestUtils;

import java.util.ArrayList;
import java.util.List;

public final class ReaderUtils {

    public ReaderUtils() {
        // Private constructor for utility class
    }

    public static <I> List<I> readAll(StepExecution stepExecution, ItemReader<I> itemReader) throws Exception {

        return StepScopeTestUtils.doInStepScope(stepExecution, () -> {

            List<I> items = new ArrayList<>();

            ItemStreamReader<I> streamReader = (itemReader instanceof ItemStreamReader<?>)
                    ? (ItemStreamReader<I>) itemReader
                    : null;

            ExecutionContext executionContext = stepExecution.getExecutionContext();

            if (streamReader != null) {
                streamReader.open(executionContext);
            }

            try {
                I item;
                while ((item = itemReader.read()) != null) {
                    items.add(item);
                    if (streamReader != null) {
                        streamReader.update(executionContext);
                    }
                }
            } finally {
                if (streamReader != null) {
                    streamReader.close();
                }
            }

            return items;
        });
    }

}
