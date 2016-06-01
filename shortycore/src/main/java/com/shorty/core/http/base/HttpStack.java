package com.shorty.core.http.base;

import com.shorty.core.http.action.HttpAction;
import com.shorty.core.http.action.ShortyHttpResponse;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * Created by yue.huang on 2016/5/11.
 */
public abstract class HttpStack {
    protected final static String HEADER_CONTENT_TYPE = "Content-Type";
    protected final static String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    protected final static String USER_AGENT = "User-Agent";
    protected final static String ENCODING_GZIP = "gzip";
    protected final static int CONNECTION_TIME_OUT_MS = 15000;

    public abstract ShortyHttpResponse performRequest(HttpAction action, Map<String, String> additionalHeaders) throws IOException, NoSuchAlgorithmException, KeyManagementException;
}
