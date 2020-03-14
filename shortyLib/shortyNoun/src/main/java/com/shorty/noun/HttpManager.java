package com.shorty.noun;

import android.os.Environment;

import com.shorty.logger.Logger;
import com.shorty.noun.event.EventListener;
import com.shorty.noun.parser.DefaultResultParser;
import com.shorty.noun.parser.ResultParser;
import com.shorty.test.annotation.UnitTest;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * http 通讯类
 *
 * @author yuehuang
 * @version 1.0
 * @since 2020/3/5
 */
public class HttpManager {
    //20M
    private final static long CACHE_MAX_SIZE = 20 * 1024 * 1024;

    private static HttpManager httpManager;
    private OkHttpClient client;
    private Cache cache;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private ResultParser parser;

    private HttpManager(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        try {
            String diskPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            String cacheDir = diskPath + File.separatorChar + "cache";
            File cacheDirectory = new File(cacheDir);
            if (!cacheDirectory.exists()) {
                cacheDirectory.mkdir();
            }
            cache = new Cache(cacheDirectory, CACHE_MAX_SIZE);
            builder.cache(cache);
        }catch (Exception e){
//            Logger.e(e);
        }
        client = builder.build();
        parser = new DefaultResultParser();
    }

    public static HttpManager getHttpManager() {
        if (httpManager == null) {
            httpManager = new HttpManager();
        }

        return httpManager;
    }

    public void setResultParser(ResultParser parser){
        this.parser = parser;
    }

    public void setCache(File cacheDirectory, long maxSize) throws IOException {
        if (cache != null) {
            cache.delete();
        }
        if (!cacheDirectory.exists()) {
            cacheDirectory.mkdir();
        }
        cache = new Cache(cacheDirectory, maxSize);
    }

    public void clearCache() throws IOException {
        if (cache != null) {
            cache.delete();
        }
    }

    @UnitTest(intput = "#url='https://www.aishuke123.com/shuke/utk', #json=''", aync = true)
    public void post(String url, String json, EventListener listener) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Logger.e(e);
                parser.onFailure(call, listener);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                parser.onResponse(response, listener);
            }
        });

//        return null;
//        try (Response response = client.newCall(request).execute()) {
//            return response.body().string();
//        }
    }

//    public static void main(String[] args) throws IOException {
//        HttpManager httpManager = new HttpManager();
//        httpManager.setCache(new File("cache"), CACHE_MAX_SIZE);
//        String s = httpManager.post("https://www.aishuke123.com/shuke/utk", "");
//        System.out.println(s);
//    }
}
