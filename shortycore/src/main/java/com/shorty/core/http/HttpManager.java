package com.shorty.core.http;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;

import com.shorty.core.http.base.BaseParse;
import com.shorty.core.http.action.HttpAction;
import com.shorty.core.http.base.HttpStack;
import com.shorty.core.http.action.ShortyHttpResponse;
import com.shorty.core.http.constant.HttpStatus;
import com.shorty.core.http.parse.JsonParse;
import com.shorty.core.http.stack.HttpClientStack;
import com.shorty.core.http.stack.HttpConnectionStack;
import com.shorty.core.manager.BaseManager;
import com.shorty.core.utils.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * for http request
 * Created by yue.huang on 2016/5/11.
 */
public class HttpManager extends BaseManager{
    private ExecutorService executor;
    private Handler mHandler;
    private final static Class DEFAULT_PARSE_CLASS = JsonParse.class;
    private Class parseClass;

    private String userAgent;
    private Map<String, String> addHeaders;

    @Override
    public void onCreate(Context context) {
        executor = Executors.newCachedThreadPool();
        addHeaders = new HashMap<String, String>();
        mHandler = new Handler(context.getMainLooper());
        String packageName = context.getPackageName();
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Logger.e(e);
        }
    }

    @Override
    public void onDestroy() {
        executor.shutdown();
        executor = null;
    }

    public void setParseClass(Class<BaseParse> cls){
        parseClass = cls;
    }

    /**
     * submit http request
     * @param action
     */
    public void submit(final HttpAction action) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                syncSubmit(action);
            }
        });
    }

    public void syncSubmit(final HttpAction action) {
        try {
            HttpStack stack = null;
            if(Build.VERSION.SDK_INT >= 14) {
                stack = new HttpConnectionStack(userAgent);
            } else {
                stack = new HttpClientStack(userAgent);
            }
            Logger.i("Http req -> " + action.getUrl() + " params: " + action.getParams());
            final ShortyHttpResponse response = stack.performRequest(action, action.getHeaders());
            if(action.getHttpActionListener() == null) {
                return;
            }

            if(response.statusCode == HttpStatus.SC_OK) {
                try {
                    Class parseClass = DEFAULT_PARSE_CLASS;
                    if (HttpManager.this.parseClass != null){
                        parseClass = HttpManager.this.parseClass;
                    }
                    BaseParse parse = (BaseParse) parseClass.newInstance();
                    parse.setHandler(mHandler);
                    parse.parse(response, action.getHttpActionListener());
                } catch (Exception e) {
                    Logger.e(e);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            action.getHttpActionListener().onFailure(-1, "网络请求错误");
                        }
                    });

                }
            } else {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        action.getHttpActionListener().onFailure(response.statusCode, "网络请求错误" + response.statusCode);
                    }
                });
            }
        } catch (Exception e) {
            Logger.e(e);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    action.getHttpActionListener().onFailure(-1, "网络请求失败");
                }
            });
        }
    }
}
