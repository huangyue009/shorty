package com.shorty.core.http.stack;

import com.shorty.core.http.action.HttpAction;
import com.shorty.core.http.action.MultiPartAction;
import com.shorty.core.http.action.ShortyHttpResponse;
import com.shorty.core.http.base.HttpStack;
import com.shorty.core.http.constant.HttpMethodType;
import com.shorty.core.http.multipart.MultipartEntity;
import com.shorty.core.http.ssl.IgnoreCertTrustManager;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * HttpConnection with request
 * user after api 14
 * Created by yue.huang on 2016/5/24.
 */
public class HttpConnectionStack extends HttpStack {
    private String userAgent;
    private SSLSocketFactory sslSocketFactory;

    public HttpConnectionStack(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public ShortyHttpResponse performRequest(HttpAction action, Map<String, String> additionalHeaders) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        HttpMethodType methodType = action.getHttpMethodType();
        String url = methodType.URL;
        HashMap<String, String> map = new HashMap<String, String>();
        // chenbo add gzip support,new user-agent
        if (action.isShouldGzip()) {
            map.put(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
        }
        map.put(USER_AGENT, userAgent);
        // end
        map.putAll(action.getHeaders());
        map.putAll(additionalHeaders);
        URL parsedUrl = new URL(url);
        HttpURLConnection connection = openConnection(parsedUrl, action);

        for (String headerName : map.keySet()) {
            connection.addRequestProperty(headerName, map.get(headerName));

        }

//        if (request instanceof MultiPartRequest) {
//            setConnectionParametersForMultipartRequest(connection, request);
//        } else {
//        }
        setConnectionParametersForRequest(connection, action);
        // Initialize HttpResponse with data from the HttpURLConnection.
        int responseCode = connection.getResponseCode();
        if (responseCode == -1) {
            // -1 is returned by getResponseCode() if the response code could not be retrieved.
            // Signal to the caller that something was wrong with the connection.
            throw new IOException("Could not retrieve response code from HttpUrlConnection.");
        }

        ShortyHttpResponse resp = null;
        InputStream inputStream;
        try {
            inputStream = connection.getInputStream();
        } catch (IOException ioe) {
            inputStream = connection.getErrorStream();
        }

        if (ENCODING_GZIP.equalsIgnoreCase(connection.getContentEncoding())) {
            resp = new ShortyHttpResponse(responseCode, new GZIPInputStream(inputStream), getHeaders(connection));
        }

        if (resp == null) {
            resp = new ShortyHttpResponse(responseCode, inputStream, getHeaders(connection));
        }

        return resp;
    }

    private Map<String, String> getHeaders(HttpURLConnection connection) {
        Map<String, String> headers = new HashMap<String, String>();
        for (Map.Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
            if (header.getKey() != null) {
                if (header.getKey() != null) {
                    String value = "";
                    for (String head : header.getValue()) {
                        value += head + ";";
                    }

                    if (value.length() > 0) {
                        value = value.substring(0, value.length() - 1);
                    }
                    headers.put(header.getKey(), value);
                }
            }
        }

        return headers;
    }

    private void setConnectionParametersForRequest(HttpURLConnection connection,
                                                   HttpAction action) throws IOException {
        switch (action.getHttpMethodType().type) {
            case HttpAction.GET:
                connection.setRequestMethod("GET");
                break;
            case HttpAction.DELETE:
                connection.setRequestMethod("DELETE");
                break;
            case HttpAction.POST: {
                connection.setRequestMethod("POST");
                addBodyIfExists(connection, action);
                break;
            }
            case HttpAction.PUT: {
                connection.setRequestMethod("PUT");
                addBodyIfExists(connection, action);
                break;
            }
            default:
                throw new IllegalStateException("Unknown method type.");
        }
    }

    /**
     * Opens an {@link HttpURLConnection} with parameters.
     *
     * @param url
     * @return an open connection
     * @throws IOException
     */
    private HttpURLConnection openConnection(URL url, HttpAction action) throws IOException, KeyManagementException, NoSuchAlgorithmException {
        HttpURLConnection connection = createConnection(url);

        int timeoutMs = (int) action.getTimeoutMs();
        connection.setConnectTimeout(CONNECTION_TIME_OUT_MS);
        connection.setReadTimeout(timeoutMs);
        connection.setUseCaches(false);
        connection.setDoInput(true);


        // use caller-provided custom SslSocketFactory, if any, for HTTPS
        if (action.isSSL()) {
            if(sslSocketFactory == null){
                sslSocketFactory = createIgnoreSSLContext().getSocketFactory();
            }

            ((HttpsURLConnection) connection).setSSLSocketFactory(sslSocketFactory);
        }

        return connection;
    }

    /*
 *  Creates SSLContext and initialises with the IgnoreTrustManager to trust all secured server. Basically ignoring the trust to connect to server.
 */
    private static SSLContext createIgnoreSSLContext() throws IOException, NoSuchAlgorithmException, KeyManagementException {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, new TrustManager[]{new IgnoreCertTrustManager()}, null);
        return context;
    }

    /**
     * Create an {@link HttpURLConnection} for the specified {@code url}.
     */
    protected HttpURLConnection createConnection(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }

    private static void addBodyIfExists(HttpURLConnection connection, final HttpAction action)
            throws IOException {
        connection.setDoOutput(true);
        connection.addRequestProperty(HEADER_CONTENT_TYPE, action.getBodyContentType());

        if (action instanceof MultiPartAction) {
            MultipartEntity multipartEntity = ((MultiPartAction) action).getMultipartEntity();
            // 防止所有文件写到内存中
            connection.setFixedLengthStreamingMode((int)multipartEntity.getContentLength());
            multipartEntity.writeTo(connection.getOutputStream());
        } else {
            byte[] body = action.getBody();
            if (body != null) {
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.write(body);
                out.close();
            }
        }
    }
}
