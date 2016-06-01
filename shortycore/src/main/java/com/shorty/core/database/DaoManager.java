package com.shorty.core.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.shorty.core.annotation.DatabaseField;
import com.shorty.core.annotation.DatabaseTable;
import com.shorty.core.manager.BaseManager;
import com.shorty.core.utils.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 * so name
 * Created by yue.huang on 2016/4/14.
 */
public class DaoManager extends BaseManager {
    private DaoHelper helper;
    private Hashtable<String, BaseDao> mDBHash;
    private HashMap<String, HashMap<String, Integer>> entryIndexMap;

    @Override
    public void onCreate(Context context) {
        mDBHash = new Hashtable<String, BaseDao>();
        entryIndexMap = new HashMap<String, HashMap<String, Integer>>();
    }

    /**
     * query entry list
     * @param entryClass
     * @param selection
     * @param args
     * @param <T>
     * @return
     */
    public <T extends List<?>> T query(Class entryClass, String selection, String[] args){
        List<?> list = new ArrayList();
        SQLiteDatabase db = helper.getWritableDatabase();
        String table = getTableName(entryClass);
        if(!TextUtils.isEmpty(table)) {
            Cursor cursor = db.rawQuery("select * from " + table + " where " + selection, args);

            cursor.close();
        }
        return (T) list;
    }

    public <K extends Object> K getEntry(Class entryClass, Cursor cursor) {
        if(cursor == null && cursor.getCount() > 0 && cursor.moveToFirst()){

        }

        return null;
    }

    public void setHelper(DaoHelper helper){
        this.helper = helper;
    }

    public DaoHelper getHelper() {
        return helper;
    }

    @Override
    public void onDestroy() {
        helper.close();
    }

    public void dropTable(Class tableCls) {
        helper.deleteTable(tableCls);
    }

    private String getTableName(Class tableClass){
        Annotation databaseTable = tableClass.getAnnotation(DatabaseTable.class);
        if(databaseTable != null && !TextUtils.isEmpty(((DatabaseTable) databaseTable).tableName())) {
            String tableName = ((DatabaseTable) databaseTable).tableName();
            if (!TextUtils.isEmpty(tableName)) {
                return tableName;
            }
        }
        return null;
    }

    private String getColumnName(Field field) {
        if (field != null && field.getAnnotation(DatabaseField.class) instanceof DatabaseField) {
            DatabaseField databaseField = field.getAnnotation(DatabaseField.class);
            if (TextUtils.isEmpty(databaseField.columnName())) {
                return field.getName().toLowerCase();
            } else {
                return databaseField.columnName().toLowerCase();
            }
        }

        return null;
    }

    /**
     * get Database with dao class
     *
     * @return
     * @user yue.huang
     */
    public BaseDao getDatabase(Class<? extends BaseDao> ownerClass) {
        if (ownerClass == null) {
            return null;
        }

        if (!mDBHash.containsKey(ownerClass.getName())) {
            try {
                Constructor<?> managerConst = ownerClass.getDeclaredConstructor();
                managerConst.setAccessible(true);

                BaseDao bm = (BaseDao) managerConst.newInstance();
                bm.onCreate(this);
                mDBHash.put(ownerClass.getName(), bm);
            } catch (Exception e) {
                Logger.e(e);
            }
        }

        return mDBHash.get(ownerClass.getName());
    }
}
