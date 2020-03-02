package com.shorty.demo;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.shorty.encrypt.ShortyEncrypt;
import com.shorty.logger.Logger;


class SingletonDemo {
    private static SingletonDemo instance=new SingletonDemo();
    private SingletonDemo(){

    }
    public static SingletonDemo getInstance(){
        return instance;
    }
}

@Route(path = "/demo/main")
public class MainActivity extends AppCompatActivity {
    public static Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ShortyEncrypt shortyEncrypt = ShortyEncrypt.Companion.getShortyEncrypt();
//                String s = "no thread info and only 2 method";
                        byte[] e = shortyEncrypt.aesEncrypt("no thread info and only 2 method".getBytes());
//                Logger.w("EncryptYUan = " + s);
                        Logger.w("Encrypt = " + new String(e));

                        String e2 = new String(shortyEncrypt.aesDecrypt(e));
                        Logger.w("Decrypt = " + e2);


                        e = shortyEncrypt.aesEncrypt("no thread info and only 2 method1".getBytes(), "123456");
//                Logger.w("EncryptYUan = " + s);
                        Logger.w("Encrypt2 = " + new String(e));

                        e2 = new String(shortyEncrypt.aesDecrypt(e, "123456"));
                        Logger.w("Decrypt2 = " + e2);
//                Logger.clearDiskLog(1);
                    }
                } ).start();
            }
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
