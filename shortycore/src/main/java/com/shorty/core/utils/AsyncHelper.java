package com.shorty.core.utils;

import android.os.AsyncTask;

/**
 * Created by yue.huang on 2016/6/3.
 */
public class AsyncHelper {
    public static void execute(Runnable runnable) {
        new ShortyTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, runnable);
    }

    private static class ShortyTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            ((Runnable)objects[0]).run();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {

        }
    }
}
