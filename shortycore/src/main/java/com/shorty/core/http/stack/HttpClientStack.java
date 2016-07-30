package com.shorty.core.http.stack;

import android.net.http.AndroidHttpClient;

import com.shorty.core.http.action.HttpAction;
import com.shorty.core.http.action.MultiPartAction;
import com.shorty.core.http.action.ShortyHttpResponse;
import com.shorty.core.http.base.HttpStack;
import com.shorty.core.http.multipart.MultipartEntity;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * HttpClient with request
 * user before api 14
 * Created by yue.huang on 2016/5/24.
 */
public class HttpClientStack extends HttpStack {
    private HttpClient httpClient;
    private String userAgent;

    public HttpClientStack(String userAgent){
        this.userAgent = userAgent;
    }

    @Override
    public ShortyHttpResponse performRequest(HttpAction action, Map<String, String> additionalHeaders) throws IOException {
        HttpUriRequest httpRequest = createHttpRequest(action);
        if (action.isShouldGzip()) {
            httpRequest.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
        }
        addHeaders(httpRequest, additionalHeaders);
        HttpParams httpParams = httpRequest.getParams();
        long timeoutMs = action.getTimeoutMs();
        // TODO: Reevaluate this connection timeout based on more wide-scale
        // data collection and possibly different for wifi vs. 3G.
        HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIME_OUT_MS);
        HttpConnectionParams.setSoTimeout(httpParams, (int) timeoutMs);

        if(action.isSSL()){
            SchemeRegistry registry = new SchemeRegistry(); // SchemeRegistry类用来维持一组Scheme，
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
            if(httpClient == null) {
                httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(httpParams, registry), httpParams);
            }
        } else {
            if(httpClient == null) {
                httpClient = AndroidHttpClient.newInstance(userAgent);
            }
        }

        HttpResponse response = httpClient.execute(httpRequest);
        ShortyHttpResponse resp = null;
        if (response != null) {
            final HttpEntity entity = response.getEntity();
            if (entity == null) {
                return null;
            }
            final Header encoding = entity.getContentEncoding();
            if (encoding != null) {
                for (HeaderElement element : encoding.getElements()) {
                    if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
                        resp = new ShortyHttpResponse(response.getStatusLine().getStatusCode(), new GZIPInputStream(entity.getContent()), getHeaders(response.getAllHeaders()));
                        break;
                    }
                }
            }

            if(resp == null){
                resp = new ShortyHttpResponse(response.getStatusLine().getStatusCode(), entity.getContent(), getHeaders(response.getAllHeaders()));
            }
        }
        return resp;
    }

    private Map<String, String> getHeaders(Header[] headers){
        Map<String, String> headerMap = new HashMap<String, String>();
        if(headers != null) {
            for(Header h : headers){
                headerMap.put(h.getName(), h.getValue());
            }
        }

        return headerMap;
    }

    private HttpUriRequest createHttpRequest(HttpAction action) throws IOException {
        int requestType = action.getRequestType();
        switch (requestType) {
            case HttpAction.GET:
                return new HttpGet(action.getUrl());
            case HttpAction.DELETE:
                return new HttpDelete(action.getUrl());
            case HttpAction.POST: {
                HttpPost postRequest = new HttpPost(action.getUrl());
                setEntityIfNonEmptyBody(postRequest, action);
                return postRequest;
            }
            case HttpAction.PUT: {
                HttpPut putRequest = new HttpPut(action.getUrl());
                setEntityIfNonEmptyBody(putRequest, action);
                return putRequest;
            }
//            case Method.HEAD:
//                return new HttpHead(request.getUrl());
//            case Method.OPTIONS:
//                return new HttpOptions(request.getUrl());
//            case Method.TRACE:
//                return new HttpTrace(request.getUrl());
//            case Method.PATCH: {
//                HttpPatch patchRequest = new HttpPatch(request.getUrl());
//                patchRequest.addHeader(HEADER_CONTENT_TYPE, request.getBodyContentType());
//                setEntityIfNonEmptyBody(patchRequest, request);
//                return patchRequest;
//            }
            default:
                throw new IllegalStateException("Unknown request method.");
        }
    }

    private void addHeaders(HttpUriRequest httpRequest, Map<String, String> headers) {
        for (String key : headers.keySet()) {
            httpRequest.setHeader(key, headers.get(key));
        }
    }

    private static void setEntityIfNonEmptyBody(HttpEntityEnclosingRequestBase httpRequest, HttpAction action)
            throws IOException {
        if (action instanceof MultiPartAction) {
            MultipartEntity multipartEntity = ((MultiPartAction) action).getMultipartEntity();
            httpRequest.setEntity(multipartEntity);
        } else {
            byte[] body = action.getBody();
            if (body != null) {
                HttpEntity entity = new ByteArrayEntity(body);
                httpRequest.setEntity(entity);
            }
        }
    }
}
