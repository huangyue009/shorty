package com.shorty.noun.parser;

import com.shorty.noun.event.EventListener;

import okhttp3.Call;
import okhttp3.Response;

/**
 * @author yuehuang
 * @version 1.0
 * @since 2020/3/13
 */
public abstract class ResultParser {
    public abstract void onFailure(Call call, EventListener listener);

    public abstract void onResponse(Response response, EventListener listener);
}
