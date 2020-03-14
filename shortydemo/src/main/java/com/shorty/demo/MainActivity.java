package com.shorty.demo;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.shorty.logger.Logger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


@Route(path = "/demo/main")
public class MainActivity extends AppCompatActivity {
    public static Activity activity;
    /**
     * 从flutter这边传递数据到Android
     */
    public static final String FLUTTER_TO_ANDROID_CHANNEL = "com.shorty.demo.toandroid/plugin";
    /**
     * 从Android这边传递数据到flutter
     */
    public static final String ANDROID_TO_FLUTTER_CHANNEL = "com.shorty.demo.toflutter/plugin";
    /**
     * 应用场景：以前两种都不一样，互相调用
     */
    public static final String ANDROID_AND_FLUTTER_CHANNEL = "com.shorty.demo.androidAndFlutter/plugin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.fab2).setOnClickListener(view -> {
            ARouter.getInstance().build("/test2/main").navigation();
//            startActivity(new Intent(MainActivity.this, com.example.fluttertest2.host.MainActivity.class));
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

//                startActivity(new Intent(MainActivity.this, com.example.fluttermodule.host.MainActivity.class));
            // 1. 普通跳转
//                ARouter.getInstance().build("/test/activity").navigation();
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        ShortyEncrypt shortyEncrypt = ShortyEncrypt.Companion.getShortyEncrypt();
////                String s = "no thread info and only 2 method";
//                        byte[] e = shortyEncrypt.aesEncrypt("no thread info and only 2 method".getBytes());
////                Logger.w("EncryptYUan = " + s);
//                        Logger.w("Encrypt = " + new String(e));
//
//                        String e2 = new String(shortyEncrypt.aesDecrypt(e));
//                        Logger.w("Decrypt = " + e2);
//
//
//                        e = shortyEncrypt.aesEncrypt("no thread info and only 2 method1".getBytes(), "123456");
////                Logger.w("EncryptYUan = " + s);
//                        Logger.w("Encrypt2 = " + new String(e));
//
//                        e2 = new String(shortyEncrypt.aesDecrypt(e, "123456"));
//                        Logger.w("Decrypt2 = " + e2);
////                Logger.clearDiskLog(1);
//                    }
//                } ).start();
        });
        Logger.w("no thread info and only 1 method");


        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Logger.w("no thread info and only 2 method");
                return null;
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
