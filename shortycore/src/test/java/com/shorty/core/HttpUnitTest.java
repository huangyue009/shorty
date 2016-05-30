package com.shorty.core;

import com.shorty.core.http.action.HttpActionListener;
import com.shorty.core.http.action.ShortyHttpResponse;
import com.shorty.core.http.parse.JsonParse;
import com.shorty.core.testEntry.TestEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.StringBufferInputStream;

/**
 * Created by yue.huang on 2016/5/24.
 */
public class HttpUnitTest {
    @Test
    public void jsonParse_parse() throws JSONException {
        JsonParse parse = new JsonParse();
        HttpActionListener<TestEntry> listener = new HttpActionListener<TestEntry>() {
            @Override
            public void onSuccess(TestEntry result) {
            }

            @Override
            public void onFailure(int resultCode, String error) {
            }
        };
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("a", 11);
        jsonObject.put("b", 1.1);
        jsonObject.put("c", "aaa");
        jsonObject.put("d", true);
        JSONArray array = new JSONArray();
        array.put("a");
        array.put("b");
        array.put("c");
        array.put("d");
        jsonObject.put("e", array);

        ShortyHttpResponse response = new ShortyHttpResponse(new StringBufferInputStream(jsonObject.toString()));
        parse.parse(response, listener);
    }


}
