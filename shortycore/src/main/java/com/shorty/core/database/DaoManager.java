package com.shorty.core.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.text.TextUtils;

import com.shorty.core.manager.BaseManager;
import com.shorty.core.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * so name
 * Created by yue.huang on 2016/4/14.
 */
public class DaoManager extends BaseManager {
    private DaoHelper helper;
    private HashMap<String, TableEntry> entryIndexMap;

    private static final String DB_NAME = "roiland_air_cleaner";
    private static final int DB_VERSION = 1;

    @Override
    public void onCreate(Context context) {
        entryIndexMap = new HashMap<String, TableEntry>();
        helper = new DaoHelper(context.getApplicationContext(), DB_NAME, null, DB_VERSION);
    }

    /**
     * 创建数据库，如果已有同名的表则不创建表
     * @param tableClass
     */
    public void createTable(Class tableClass){
        TableEntry table = helper.createTable(tableClass);
        if(!entryIndexMap.containsKey(tableClass.getName()) && table != null){
            entryIndexMap.put(tableClass.getName(), table);
        }
    }

    /**
     * query entry list
     *
     * @param entryClass
     * @param selection
     * @param args
     * @param <T>
     * @return
     */
    public <T extends List<?>> T query(Class<?> entryClass, String selection, String[] args) {
        if (!entryIndexMap.containsKey(entryClass.getName())) {
            entryIndexMap.put(entryClass.getName(), helper.getTableEntry(entryClass));
        }

        List list = new ArrayList();
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            String table = entryIndexMap.get(entryClass.getName()).tableName;
            if (!TextUtils.isEmpty(table)) {
                String sql = "select * from " + table;
                if(!TextUtils.isEmpty(selection)){
                    sql += " where " + selection;
                }
                Cursor cursor = db.rawQuery(sql, args);
                if(cursor.moveToFirst()){
                    list.add(helper.getEntry(entryClass, cursor));
                    while (cursor.moveToNext()){
                        list.add(helper.getEntry(entryClass, cursor));
                    }
                }

                cursor.close();
            }
        } catch (Exception e) {
            Logger.e(e);
        }
        return (T) list;
    }

    public HashMap<String, TableEntry> getEntryIndexMap() {
        return entryIndexMap;
    }

    /**
     * 根据id
     *
     * @param entryClass
     * @param id
     * @param <K>
     * @return
     */
    public <K extends Object> K getEntryById(Class<K> entryClass, long id) throws InstantiationException, IllegalAccessException {
        if (!entryIndexMap.containsKey(entryClass.getName())) {
            entryIndexMap.put(entryClass.getName(), helper.getTableEntry(entryClass));
        }

        SQLiteDatabase db = helper.getWritableDatabase();
        String table = entryIndexMap.get(entryClass.getName()).tableName;
        if (!TextUtils.isEmpty(table)) {
            Cursor cursor = db.rawQuery(String.format("select * from " + table + " where %s=?", BaseEntry.COLUMN_KEY_ID), new String[]{id + ""});
            if (cursor.moveToFirst() && !cursor.isAfterLast()) {
                return (K) helper.getEntry(entryClass, cursor);
            }
            cursor.close();
        }
        return null;
    }

    public boolean delete(Class table, String selection, String[] args){
        if (!entryIndexMap.containsKey(table.getName())) {
            entryIndexMap.put(table.getName(), helper.getTableEntry(table));
        }

        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            TableEntry tableEntry = entryIndexMap.get(table.getName());
            db.delete(tableEntry.tableName, selection, args);

            return true;
        } catch (Exception e) {
            Logger.e(e);
        }

        return false;
    }

    public boolean delete(BaseEntry entry){
        if (!entryIndexMap.containsKey(entry.getClass().getName())) {
            entryIndexMap.put(entry.getClass().getName(), helper.getTableEntry(entry.getClass()));
        }

        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            TableEntry tableEntry = entryIndexMap.get(entry.getClass().getName());
            if(entry.id != null) {
                db.delete(tableEntry.tableName, BaseEntry.COLUMN_KEY_ID + "=?", new String[]{entry.id.toString()});
            } else if(tableEntry.unioue){
                Object value = entry.getClass().getField(tableEntry.colunmName.get(tableEntry.unioueColunmName)).get(entry);
                db.delete(tableEntry.tableName, tableEntry.unioueColunmName + "=?", new String[]{value.toString()});
            }

            return true;
        } catch (Exception e) {
            Logger.e(e);
        }

        return false;
    }

    public boolean save(final BaseEntry entry) {
        if (!entryIndexMap.containsKey(entry.getClass().getName())) {
            entryIndexMap.put(entry.getClass().getName(), helper.getTableEntry(entry.getClass()));
        }

        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            TableEntry tableEntry = entryIndexMap.get(entry.getClass().getName());
            ContentValues contentValues = new ContentValues();
            Iterator<Map.Entry<String, String>> itr = tableEntry.colunmName.entrySet().iterator();
            while (itr.hasNext()) {
                Map.Entry<String, String> entry2 = itr.next();
                Object value = entry.getClass().getField(entry2.getValue()).get(entry);
                contentValues.put(entry2.getKey(), value == null ? null : value.toString());
            }

            long rowIndex = -1;
            if (tableEntry.unioue) {
                rowIndex = db.replace(tableEntry.tableName, null, contentValues);
            } else if (entry.id != null) {
                return db.update(tableEntry.tableName, contentValues, BaseEntry.COLUMN_KEY_ID + "=" + entry.id, null) > 0;
            } else {
                rowIndex = db.insert(tableEntry.tableName, null, contentValues);
            }

            if(rowIndex != -1){
                entry.id = (int)rowIndex;
                return true;
            }
        } catch (Exception e) {
            Logger.e(e);
        }

        return false;
    }

    public void setHelper(DaoHelper helper) {
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
}
