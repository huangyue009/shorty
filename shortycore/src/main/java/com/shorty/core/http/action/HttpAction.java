package com.shorty.core.http.action;

import com.shorty.core.http.base.BaseParse;
import com.shorty.core.utils.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * http network request class
 *
 * @author huangyue  2013-10-28
 */
public class HttpAction {
    public static final int POST = 1;
    public static final int GET = 2;
    public static final int PUT = 3;
    public static final int DELETE = 4;//ADD by Lychee.lin on 2016-01-06

    protected static String DEFAULT_PARAMS_ENCODING = "UTF-8";
    private static final long DEFAULT_TIMEOUT_MS = 15000;

    protected HashMap<String, Object> params;
    private JSONObject obj;
    private HttpActionListener<?> actionListener;
    private boolean shouldGzip = true;
    private boolean isSSL = true;
    private long timeoutMs;
    protected Map<String, String> headers;
    private String url;
    private int requestType;
    private Class<? extends BaseParse> parseClass;

    public HttpAction(String url, int requestType) {
        params = new HashMap<String, Object>();
        headers = new HashMap<String, String>();
        this.url = url;
        this.requestType = requestType;
        if(url.startsWith("https://")){
            isSSL = true;
        } else {
            isSSL = false;
        }
    }

    public HttpAction(String url, int requestType, Class<? extends BaseParse> parseClass) {
        params = new HashMap<String, Object>();
        headers = new HashMap<String, String>();
        this.url = url;
        this.requestType = requestType;
        this.parseClass = parseClass;
        if(url.startsWith("https://")){
            isSSL = true;
        } else {
            isSSL = false;
        }
    }


    public Map<String, String> getHeaders(){
        return headers;
    }

    public void setHeader(String key, String value){
        headers.put(key, value);
    }

    public Class<? extends BaseParse> getParseClass(){
        return parseClass;
    }

    /**
     * 请求方法类型
     *
     * @return
     */
    public int getRequestType() {
        return requestType;
    }


    public String getUrl(){
        if(url.endsWith("&")){
            url = url.substring(0, url.length() - 1);
        }
        return this.url;
    }
    /**
     * 设置提交结果监听
     *
     * @param listener
     */
    public void setHttpActionListener(HttpActionListener<?> listener) {
        actionListener = listener;
    }

    /**
     * 获得监听器
     *
     * @return
     */
    public HttpActionListener<?> getHttpActionListener() {
        return actionListener;
    }

    public void setEncoding(String encoding){
        DEFAULT_PARAMS_ENCODING = encoding;
    }

    public String getEncoding(){
        return DEFAULT_PARAMS_ENCODING;
    }


    public JSONObject getJson() {
        return obj;
    }

    public void putAll(JSONObject json) {
        obj = json;
    }

    public void setTimeoutMs(long timeout) {
        timeoutMs = timeout;
    }
    public long getTimeoutMs() {
        return timeoutMs == 0 ? DEFAULT_TIMEOUT_MS : timeoutMs;
    }

    public String getBodyContentType() {
        if (obj != null) {
            return "application/json; charset=" + getEncoding();
        } else {
            return "application/x-www-form-urlencoded; charset=" + getEncoding();
        }
    }

    /**
     * 获得指定参数
     *
     * @param key
     * @return
     */
    public Object getJsonParam(String key) {
        if (obj == null) {
            obj = new JSONObject();
        }
        try {
            if (key == null || obj.isNull(key)) {
                return null;
            }

            return obj.get(key);
        } catch (JSONException e) {
            Logger.e(e);
        }

        return null;
    }

    /**
     * 增加json的参数,当请求类型是post或者put时使用
     *
     * @param key
     * @param value
     */
    public void putJson(String key, Object value) {
        if (obj == null) {
            obj = new JSONObject();
        }
        try {
            if (value == null) {
                obj.remove(key);
                return;
            }
            obj.put(key, value);
        } catch (JSONException e) {
            Logger.e(e);
        }
    }

    public boolean isShouldGzip() {
        return shouldGzip;
    }

    public void setShouldGzip(boolean shouldGzip) {
        this.shouldGzip = shouldGzip;
    }

    public boolean isSSL() {
        return isSSL;
    }

    public void setSSL(boolean isSSL) {
        this.isSSL = isSSL;
    }

    /**
     * 上传文件参数,当请求类型是get时使用
     *
     * @param key
     * @param value
     */
    public void putParam(String key, String value) {
        params.put(key, value);
        if(requestType == GET){
            try {
                StringBuffer encodedParams = new StringBuffer();
                encodedParams.append(URLEncoder.encode(key, DEFAULT_PARAMS_ENCODING));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(value, DEFAULT_PARAMS_ENCODING));
                encodedParams.append('&');
                if(!url.contains("?")){
                    url += "?";
                }
                url += encodedParams.toString();
            } catch (UnsupportedEncodingException e) {
                Logger.e(e);
            }
        }
    }

    /**
     * 获得Request的参数
     *
     * @return
     */
    public HashMap<String, Object> getParams() {
        return params;
    }

    /**
     * Returns the raw POST or PUT body to be sent.
     *
     */
    public byte[] getBody() {
        if(obj != null){
            try {
                return obj.toString().getBytes(getEncoding());
            } catch (UnsupportedEncodingException e) {
                Logger.e(e);
            }
        } else {
            Map<String, Object> params = getParams();
            if (params != null && params.size() > 0) {
                return encodeParameters(params, getEncoding());
            }
        }
        return null;
    }

    /**
     * Converts <code>params</code> into an application/x-www-form-urlencoded encoded string.
     */
    private byte[] encodeParameters(Map<String, Object> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (entry.getValue() != null) {
                    encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                    encodedParams.append('=');
                    encodedParams.append(URLEncoder.encode(entry.getValue().toString(), paramsEncoding));
                    encodedParams.append('&');
                }
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }
}

