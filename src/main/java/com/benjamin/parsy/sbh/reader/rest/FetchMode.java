package com.benjamin.parsy.sbh.reader.rest;

/**
 * Enumeration defining the fetching strategy to use for a REST-based item reader.
 *
 * <p>This enum is used to indicate how data should be retrieved from a REST API:</p>
 *
 * <ul>
 *     <li>{@link #SINGLE} — Fetches all data in a single request. Suitable for endpoints that return
 *     the complete dataset in one response.</li>
 *     <li>{@link #PAGING} — Fetches data across multiple requests, one page at a time.
 *     Used when the REST API supports pagination.</li>
 * </ul>
 */
public enum FetchMode {

    /**
     * Fetch all items in a single REST call.
     */
    SINGLE,

    /**
     * Fetch items using page-based REST calls.
     */
    PAGING

}
