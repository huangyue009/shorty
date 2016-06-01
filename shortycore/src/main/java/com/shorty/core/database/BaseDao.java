package com.shorty.core.database;

/**
 * get data list, update, insert
 * find data with id
 * Created by yue.huang on 2016/4/14.
 */
public abstract class BaseDao<T> {
    protected DaoManager manager;
    private Class actualType;

    public void onCreate(DaoManager manager){
        this.manager = manager;
//        Type[] types = ((ParameterizedType) getClass()
//                .getGenericSuperclass())
//                .getActualTypeArguments();
//        if (types == null) {
//            throw new IllegalArgumentException("Actual type arguments setting exception");
//        }
//        actualType = (Class) types[0];
//        if(helper.isAutoCreateTable()) {
//            helper.createTable(actualType);
//        }
    }

    public void onDestroy(){
    }
}
