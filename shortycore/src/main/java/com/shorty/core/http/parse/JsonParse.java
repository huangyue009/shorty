package com.shorty.core.http.parse;

import com.google.gson.Gson;
import com.shorty.core.http.action.HttpActionListener;
import com.shorty.core.http.action.ShortyHttpResponse;
import com.shorty.core.http.base.BaseParse;
import com.shorty.core.utils.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * parse json to entry
 * Created by yue.huang on 2016/5/24.
 */
public class JsonParse extends BaseParse{
    private static Gson gson;

    public JsonParse(){
        if(gson == null){
            gson = new Gson();
        }
    }

    @Override
    public void parse(ShortyHttpResponse response, final HttpActionListener listener) throws JSONException {
        final Type[] types = ((ParameterizedType) listener
                .getClass()
                .getGenericSuperclass())
                .getActualTypeArguments();
        try {
            if (types == null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onFailure(-2,
                                "数据转换类型错误！");
                    }
                });

            } else{
                String jsonStr = getContent(response.inputStream);
                Logger.i("Http resp ->  params: " + jsonStr);
                final JSONObject json = new JSONObject(jsonStr);
                final Integer result = json.getInt("result");
                String message = null;
                if(result != null && (result.intValue() == 1)) {
                    if (!json.isNull("message")) {
                        message = json.getString("message");
                    }
                    if (!json.isNull("data") ) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    listener.onSuccess(gson.fromJson(json.getString("data"), types[0]));
                                } catch (Exception e) {
                                    Logger.e(e);
                                    listener.onSuccess(null);
                                }
                            }
                        });

                        return;
                    } else if(result.intValue() == 9){
                        listener.onSuccess(null);
                        return;
                    }
                } else if(result != null){
                    if (!json.isNull("message")) {
                        message = json.getString("message");
                    }
                }

                final String msg = message;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onFailure(result, msg);
                    }
                });
            }
        } catch (Exception e) {
            if (listener != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onFailure(-3, "参数解析错误");
                    }
                });
            }
        }
    }

    private String getContent(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is)); // 初始化读缓冲区
        StringBuilder sBuilder = new StringBuilder();
        String tempStr;
        while ((tempStr = br.readLine()) != null) {
            sBuilder.append(tempStr);
        }

        return sBuilder.toString();
    }
}
