package com.benjamin.parsy.sbh.reader.rest;

import org.springframework.batch.item.ItemReader;

import java.util.Collections;
import java.util.List;

/**
 * Abstract base class for REST-based {@link ItemReader} implementations.
 *
 * <p>This class manages an internal buffer of items returned by the {@link #doRead()} method.
 * When the buffer is exhausted, {@link #doRead()} is called to refill it.
 * Subclasses must implement the {@code doRead()} method to provide the actual logic for fetching
 * a list of items from a REST source.</p>
 *
 * <p>This class handles basic iteration logic, allowing subclasses to focus on the specifics
 * of fetching and parsing REST responses.</p>
 *
 * @param <I> the type of item returned by the reader
 */
public abstract class AbstractRestItemReader<I> implements ItemReader<I> {

    /**
     * Internal buffer storing the currently fetched list of items.
     */
    private List<I> buffer = Collections.emptyList();

    /**
     * Current index in the buffer.
     */
    private int index = 0;

    /**
     * Reads a list of items from a REST endpoint. This method is called whenever the current
     * buffer is empty or exhausted.
     *
     * @return a list of items to be buffered for reading, or {@code null} or empty list if there is no more data
     */
    protected abstract List<I> doRead();

    /**
     * Returns the next item from the buffer, automatically refilling it by calling {@link #doRead()}
     * when all elements have been read.
     *
     * @return the next item, or {@code null} if no more items are available
     */
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
