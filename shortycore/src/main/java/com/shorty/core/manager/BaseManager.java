package com.shorty.core.manager;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import com.shorty.core.utils.AppUtils;

/**
 * Manager 的基类,用来承接业务层的分割
 * 
 * @author huangyue  2013-10-18
 */
public abstract class BaseManager {
    protected Context context;

	public abstract void onCreate(Context context);

	public abstract void onDestroy();
	
	/**
	 * 获得Manager的方法，用目标Manager的class获得
	 * 只能获得继承了BaseManager的类
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends BaseManager> T getManager(Class<? extends BaseManager> ownerClass) {
		return (T) ManagerFactory.getInstance().getManager(ownerClass);
	}

    /** 任意线程都可显示toast
     * @user yue.huang
     * */
    public void showToast(final String text) {
        if(TextUtils.isEmpty(text)){
            return;
        }

        AppUtils.showToastInfo(context, text);
    }

	/** 任意线程都可显示toast
	 * @user yue.huang
	 * */
	public void showToast(int res) {
		if(res > 0 && TextUtils.isEmpty(context.getString(res))){
			return;
		}

		AppUtils.showToastInfo(context, context.getString(res));
	}

	protected String getCurProcessName(Context context) {
		int pid = android.os.Process.myPid();
		ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
			if (appProcess.pid == pid) {
				return appProcess.processName;
			}
		}
		return null;
	}
}
