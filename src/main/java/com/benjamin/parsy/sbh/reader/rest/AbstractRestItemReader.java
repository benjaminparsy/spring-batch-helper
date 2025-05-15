package com.benjamin.parsy.sbh.reader.rest;

import org.springframework.batch.item.ItemReader;

import java.util.Collections;
import java.util.List;

public abstract class AbstractRestItemReader<I> implements ItemReader<I> {

    private List<I> buffer = Collections.emptyList();
    private int index = 0;

    protected abstract List<I> doRead();

    @Override
    public synchronized I read() {

        if (index >= buffer.size()) {

            buffer = doRead();
            index = 0;

            if (buffer == null || buffer.isEmpty()) {
                return null;
            }
        }

        return buffer.get(index++);
    }

}
