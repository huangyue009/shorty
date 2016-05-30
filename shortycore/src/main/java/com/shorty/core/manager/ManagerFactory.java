package com.shorty.core.manager;

import android.content.Context;

import com.shorty.core.utils.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * 所有模块Manger的工厂类
 * 
 * @author huangyue 2013-10-18
 */
public class ManagerFactory {
	private static ManagerFactory mManagerFactory;
	private static Hashtable<String, BaseManager> mMagHash;

	private ManagerFactory() {
		mMagHash = new Hashtable<String, BaseManager>();
	}

	/**
	 * 获得工厂实例
	 * 
	 * @return
	 */
	public static ManagerFactory getInstance() {
		if (mManagerFactory == null) {
			mManagerFactory = new ManagerFactory();
		}
		return mManagerFactory;
	}

    /**
     * 销毁所有管理器
     */
    public void destroy(){
        Iterator<BaseManager> itr = mMagHash.values().iterator();
        while(itr.hasNext()){
            itr.next().onDestroy();
        }
		mMagHash.clear();
		mMagHash = null;
		mManagerFactory = null;
    }

    /**
     * 销毁单个管理器
     * @param ownerClass
     */
    public void destroy(Class<? extends BaseManager> ownerClass){
        if(mMagHash.containsKey(ownerClass.getName())){
            BaseManager bm = mMagHash.remove(ownerClass.getName());
            if(bm != null){
                bm.onDestroy();
            }
        }
    }

	/**
	 * 获得Manager的方法，manager Name 需要通过 ManagerFactory的常量获得，详见ManagerFactory常量
	 * 
	 * @return
	 */
	public BaseManager getManager(Context context, Class<? extends BaseManager> ownerClass) {
		if(ownerClass == null){
			return null;
		}
		
		if(!mMagHash.containsKey(ownerClass.getName())){
			try {
				Constructor<?> managerConst = ownerClass.getDeclaredConstructor();
				managerConst.setAccessible(true);

				BaseManager bm = (BaseManager) managerConst.newInstance();
                Field field = BaseManager.class.getDeclaredField("context");
                field.setAccessible(true);
                field.set(bm, context);
                mMagHash.put(ownerClass.getName(), bm);
                bm.onCreate(context);
			} catch (Exception e) {
				Logger.e(e);
			}
		}
		
		return mMagHash.get(ownerClass.getName());
	}
}
