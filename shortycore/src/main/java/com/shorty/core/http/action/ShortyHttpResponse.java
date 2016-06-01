package com.shorty.core.http.action;

import com.shorty.core.http.constant.HttpStatus;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

/**
 * Created by yue.huang on 2016/5/11.
 */
public class ShortyHttpResponse {
    /** The HTTP status code. */
    public int statusCode;

    /** Raw data from this response. */
    public InputStream inputStream;

    /** Response headers. */
    public Map<String, String> headers;

    public ShortyHttpResponse(int statusCode, InputStream inputStream, Map<String, String> headers) {
        this.statusCode = statusCode;
        this.inputStream = inputStream;
        this.headers = headers;
    }

    public ShortyHttpResponse(InputStream inputStream) {
        this(HttpStatus.SC_OK, inputStream, Collections.<String, String>emptyMap());
    }

    public ShortyHttpResponse(InputStream inputStream, Map<String, String> headers) {
        this(HttpStatus.SC_OK, inputStream, headers);
    }
}
