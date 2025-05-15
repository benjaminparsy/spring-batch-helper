package com.benjamin.parsy.sbh.reader.rest;

import org.springframework.batch.item.ItemReaderException;

public class RestReaderException extends ItemReaderException {

    public RestReaderException(String message, Throwable cause) {
        super(message, cause);
    }

}
