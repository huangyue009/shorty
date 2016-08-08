package com.shorty.core.manager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 保存sp参数，加载error信息等
 * 
 * @author huangyue  2013-10-18
 */
public class ConfigurationManager extends BaseManager {
	private SharedPreferences mPreferences;

    @Override
    public void onCreate(Context context) {
        mPreferences = context.getSharedPreferences("shorty", Context.MODE_MULTI_PROCESS);
    }

    @Override
    public void onDestroy() {
        mPreferences = null;
    }

	public long getShareLong(String key, long defaultValue){
		return mPreferences.getLong(key, defaultValue);
	}

	public int getShareInt(String key, int defaultValue){
		return mPreferences.getInt(key, defaultValue);
	}
	
	public boolean getShareBoolean(String key, boolean defaultValue){
		return mPreferences.getBoolean(key, defaultValue);
	}
	
	public String getShareString(String key, String defaultValue){
		return mPreferences.getString(key, defaultValue);
	}
	
	public boolean putShareLong(String key, long  value){
		return mPreferences.edit().putLong(key, value).commit();
	}

	public boolean putShareInt(String key, int value){
		return mPreferences.edit().putInt(key, value).commit();
	}
	
	public boolean putShareBoolean(String key, boolean value){
		return mPreferences.edit().putBoolean(key, value).commit();
	}
	
	public boolean putShareString(String key, String value){
		return mPreferences.edit().putString(key, value).commit();
	}

}
