package com.shorty.core.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.shorty.core.annotation.DatabaseField;
import com.shorty.core.annotation.DatabaseTable;
import com.shorty.core.utils.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * shorty frame Database helper
 * can get data, create/update/del table
 * Created by yue.huang on 2016/4/11.
 */
class DaoHelper extends SQLiteOpenHelper{

    public DaoHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * delete table, when drop before update table
     * @param clsTable
     */
    protected void deleteTable(Class clsTable){
        Annotation databaseTable = clsTable.getAnnotation(DatabaseTable.class);
        if(databaseTable != null && !TextUtils.isEmpty(((DatabaseTable) databaseTable).tableName())){
            String tableName = ((DatabaseTable) databaseTable).tableName();
            commitSql("DROP TABLE " + tableName);
        }
    }

    /**
     * create table by annotations
     * @param clsTable
     */
    protected void createTable(Class clsTable){
        Annotation databaseTable = clsTable.getAnnotation(DatabaseTable.class);

        if(databaseTable != null && !TextUtils.isEmpty(((DatabaseTable) databaseTable).tableName())){
            String tableName = ((DatabaseTable) databaseTable).tableName();
            if(TextUtils.isEmpty(tableName)){
                return;
            }

            Field[] fields = clsTable.getFields();
            if(fields != null && fields.length > 0) {
                StringBuilder tableField = new StringBuilder("create table IF NOT EXISTS ");
                tableField.append(tableName + "(");
                for (int i = 0; i < fields.length; i++) {
                    if (fields[i].getAnnotation(DatabaseField.class) instanceof DatabaseField) {
                        DatabaseField databaseField = fields[i].getAnnotation(DatabaseField.class);
                        if(TextUtils.isEmpty(databaseField.columnName())){
                            tableField.append(fields[i].getName().toLowerCase() + " ");
                        } else {
                            tableField.append(databaseField.columnName().toLowerCase() + " ");
                        }

                        if(fields[i].getType().getName().equals(String.class.getName())){
                            tableField.append("text ");
                        } else {
                            tableField.append(fields[i].getType().getName().substring(fields[i].getType().getPackage().getName().length() + 1).toLowerCase() + " ");
                        }

                        if(databaseField.isPrimary()){
                            tableField.append("PRIMARY KEY ");
                        }

                        if(!databaseField.canBeNull()){
                            tableField.append("NOT NULL ");
                        }
                        tableField.append(",");
                    }
                }
                tableField.deleteCharAt(tableField.length() - 1);
                tableField.append(")");
                commitSql(tableField.toString());
            } else {
                return;
            }
        }
    }

    private void commitSql(String sql) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try{
            db.execSQL(sql);
            db.setTransactionSuccessful();
        } catch(Exception e){
            Logger.e(e);
        } finally {
            db.endTransaction();
        }
    }
}
