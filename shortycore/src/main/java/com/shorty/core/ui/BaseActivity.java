package com.shorty.core.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.shorty.core.manager.BaseManager;
import com.shorty.core.manager.ManagerFactory;
import com.shorty.core.utils.AppUtils;

import java.io.Serializable;

/**
 * base of activity frame have
 * redirect with params, show toast
 * get data and manager
 * <p>
 * UI Activity基础类，包含数据库和Manager的调用方法
 * Created by yue.huang on 16/4/9.
 */
public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * activity 跳轉方法，
     *
     * @param ActiviyClass 要跳转的目标Activity class
     * @param args         需要发送的参数，以key,value的键值对方式依次放置
     * @user yue.huang
     */
    public void redirect(Class<? extends Activity> ActiviyClass, Object... args) {
        Intent intent = new Intent(this, ActiviyClass);
        if (args != null) {
            int len = args.length;
            for (int i = 0; i < len; i += 2) {
                intent.putExtra((String) args[i], (Serializable) args[i + 1]);
            }
        }

        startActivity(intent);
    }

    /**
     * show toast with msg
     *
     * @param msg
     */
    public void showToast(String msg) {
        AppUtils.showToastInfo(this, msg);
    }

    /**
     * activity for result 跳轉方法，
     *
     * @param ActiviyClass 要跳转的目标Activity class
     * @param args         需要发送的参数，以key,value的键值对方式依次放置
     * @user yue.huang
     */
    public void redirectForResult(Class<? extends Activity> ActiviyClass, int requestCode, Object... args) {
        Intent intent = new Intent(this, ActiviyClass);
        if (args != null) {
            int len = args.length;
            for (int i = 0; i < len; i += 2) {
                intent.putExtra((String) args[i], (Serializable) args[i + 1]);
            }
        }

        startActivityForResult(intent, requestCode);
    }

    /**
     * 获得Manager的方法，用目标Manager的class获得 只能获得继承了BaseManager的类
     *
     * @return
     * @user yue.huang
     */
    @SuppressWarnings("unchecked")
    public <T extends BaseManager> T getManager(
            Class<? extends BaseManager> ownerClass) {
        return (T) ManagerFactory.getInstance().getManager(getApplicationContext(), ownerClass);
    }
}
