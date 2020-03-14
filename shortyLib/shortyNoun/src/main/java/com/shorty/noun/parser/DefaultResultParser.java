package com.shorty.noun.parser;

import com.google.gson.Gson;
import com.shorty.noun.event.EventListener;

import org.json.JSONObject;

import java.lang.reflect.Method;

import javax.annotation.Nullable;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 默认结果解析器，将结果解析单独拉出来
 * 1。提供多种解析业务的需求切换
 * 2。避免主manager的代码侵入修改
 *
 * PS:默认解析只是作为demo，具体业务需要按使用时重写
 * @author yuehuang
 * @version 1.0
 * @since 2020/3/13
 */
public class DefaultResultParser extends ResultParser {

    @Override
    public void onFailure(@Nullable Call call, @Nullable EventListener listener) {
        listener.onFailed(101, "error");
    }

    @Override
    public void onResponse(@Nullable Response response, @Nullable EventListener listener) {
        try {
            if(response.code() == 200) {
                JSONObject jsonObject = new JSONObject(response.body().string());
                int code = jsonObject.getInt("code");
                if (code == 20000) {
                    Method method = listener.getClass().getMethod("onEvent", Object.class);
                    listener.onEvent(new Gson().fromJson(jsonObject.toString(), method.getParameterTypes()[0]));
                } else {
                    listener.onFailed(code, jsonObject.getString("msg"));
                }
            } else {
                listener.onFailed(response.code(), "error coed");
            }
        } catch (Exception e) {
//            Logger.e(e);
            listener.onFailed(101, e.getMessage());
            e.printStackTrace();
        }
    }
}
