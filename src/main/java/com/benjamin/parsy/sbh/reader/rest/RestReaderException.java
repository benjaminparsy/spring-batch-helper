package com.benjamin.parsy.sbh.reader.rest;

import org.springframework.batch.item.ItemReaderException;

/**
 * Exception thrown when an error occurs during a REST-based item reading process
 * in a Spring Batch reader implementation.
 *
 * <p>This exception is typically thrown when a REST call fails (e.g., network error,
 * server error) or when the response cannot be processed (e.g., parsing failure, invalid structure).</p>
 *
 * <p>It extends {@link org.springframework.batch.item.ItemReaderException}, the standard
 * base class for exceptions thrown from item readers in Spring Batch.</p>
 */
public class RestReaderException extends ItemReaderException {

    /**
     * Constructs a new {@code RestReaderException} with the specified detail message
     * and root cause.
     *
     * @param message a descriptive message explaining the cause of the exception
     * @param cause the underlying exception that triggered this error
     */
    public RestReaderException(String message, Throwable cause) {
        super(message, cause);
    }

}
