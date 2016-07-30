package com.shorty.core.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.TextUtils;

import com.shorty.core.annotation.DatabaseField;
import com.shorty.core.annotation.DatabaseTable;
import com.shorty.core.utils.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * shorty frame Database helper
 * can get data, create/update/del table
 * Created by yue.huang on 2016/4/11.
 */
class DaoHelper extends SQLiteOpenHelper{

    DaoHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
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

    TableEntry getTableEntry(Class clsTable) {
        Annotation databaseTable = clsTable.getAnnotation(DatabaseTable.class);
        TableEntry tableEntry = new TableEntry();
        if(databaseTable != null && !TextUtils.isEmpty(((DatabaseTable) databaseTable).tableName())) {
            String tableName = ((DatabaseTable) databaseTable).tableName();
            if (TextUtils.isEmpty(tableName)) {
                return null;
            }
            tableEntry.tableName = tableName;

            Field[] fields = clsTable.getFields();
            if (fields != null && fields.length > 0) {
                for (int i = 0; i < fields.length; i++) {
                    if (fields[i].getAnnotation(DatabaseField.class) instanceof DatabaseField) {
                        DatabaseField databaseField = fields[i].getAnnotation(DatabaseField.class);
                        String tableFieldName = null;
                        if (TextUtils.isEmpty(databaseField.columnName())) {
                            tableFieldName = fields[i].getName().toLowerCase();
                        } else {
                            tableFieldName = databaseField.columnName().toLowerCase();
                        }
                        tableEntry.colunmName.put(tableFieldName, fields[i].getName());

                        if (databaseField.unioueIndex()) {
                            tableEntry.unioue = true;
                            tableEntry.unioueColunmName = tableFieldName;
                        }
                    }
                }
            }

            return tableEntry;
        }

        return null;
    }

    /**
     * create table by annotations
     * @param clsTable
     */
    TableEntry createTable(Class clsTable){
        Annotation databaseTable = clsTable.getAnnotation(DatabaseTable.class);
        TableEntry tableEntry = new TableEntry();
        if(databaseTable != null && !TextUtils.isEmpty(((DatabaseTable) databaseTable).tableName())){
            String tableName = ((DatabaseTable) databaseTable).tableName();
            if(TextUtils.isEmpty(tableName)){
                return null;
            }
            tableEntry.tableName = tableName;

            Field[] fields = clsTable.getFields();
            if(fields != null && fields.length > 0) {
                StringBuilder tableField = new StringBuilder("create table IF NOT EXISTS ");
                tableField.append(tableName + "(");
                for (int i = 0; i < fields.length; i++) {
                    if (fields[i].getAnnotation(DatabaseField.class) instanceof DatabaseField) {
                        DatabaseField databaseField = fields[i].getAnnotation(DatabaseField.class);
                        String tableFieldName = null;
                        if(TextUtils.isEmpty(databaseField.columnName())){
                            tableFieldName = fields[i].getName().toLowerCase();
                            tableField.append(tableFieldName + " ");
                        } else {
                            tableFieldName = databaseField.columnName().toLowerCase();
                            tableField.append(tableFieldName + " ");
                        }
                        tableEntry.colunmName.put(tableFieldName, fields[i].getName());

                        if(fields[i].getType().getName().equals(String.class.getName())){
                            tableField.append("text ");
                        } else {
                            tableField.append(fields[i].getType().getName().substring(fields[i].getType().getPackage().getName().length() + 1).toLowerCase() + " ");
                        }

                        if(databaseField.isPrimary()){
                            tableField.append("PRIMARY KEY AUTOINCREMENT ");
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


                for (int i = 0; i < fields.length; i++) {
                    if (fields[i].getAnnotation(DatabaseField.class) instanceof DatabaseField) {
                        DatabaseField databaseField = fields[i].getAnnotation(DatabaseField.class);

                        if(databaseField.unioueIndex()){
                            String fieldName = null;
                            if(TextUtils.isEmpty(databaseField.columnName())){
                                fieldName = fields[i].getName().toLowerCase();
                            } else {
                                fieldName = databaseField.columnName().toLowerCase();
                            }
                            commitSql(String.format("CREATE UNIQUE INDEX IF NOT EXISTS unique_index_%s ON %s(%s)", fieldName, tableName, fieldName));
                            tableEntry.unioue = true;
                            tableEntry.unioueColunmName = fieldName;
                        }
                        tableField.append(",");
                    }
                }
                return tableEntry;
            } else {
                return null;
            }
        }
        return null;
    }

    Object getEntry(Class cls, Cursor cursor) throws IllegalAccessException, InstantiationException {
        Object entry = cls.newInstance();
        Field[] fields = cls.getFields();
        if(fields != null && fields.length > 0) {
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].getAnnotation(DatabaseField.class) instanceof DatabaseField) {
                    DatabaseField databaseField = fields[i].getAnnotation(DatabaseField.class);
                    String colunmName = null;
                    if (TextUtils.isEmpty(databaseField.columnName())) {
                        colunmName = fields[i].getName().toLowerCase();
                    } else {
                        colunmName = databaseField.columnName().toLowerCase();
                    }

                    fields[i].set(entry, getValue(cursor, fields[i], colunmName));
                }
            }
        }

        return entry;
    }

    private Object getValue(Cursor cursor, Field field, String columnName){
        Class cls = field.getType();
        if(cls.getName().equals(Integer.class.getName()) || cls.getName().equals(int.class.getName())){
            return cursor.getInt(cursor.getColumnIndex(columnName));
        } else if(cls.getName().equals(Long.class.getName()) || cls.getName().equals(long.class.getName())){
            return cursor.getLong(cursor.getColumnIndex(columnName));
        } else if(cls.getName().equals(Double.class.getName()) || cls.getName().equals(double.class.getName())){
            return cursor.getDouble(cursor.getColumnIndex(columnName));
        } else if(cls.getName().equals(Float.class.getName()) || cls.getName().equals(float.class.getName())){
            return cursor.getFloat(cursor.getColumnIndex(columnName));
        } else if(cls.getName().equals(Short.class.getName()) || cls.getName().equals(short.class.getName())){
            return cursor.getShort(cursor.getColumnIndex(columnName));
        } else if(cls.getName().equals(Boolean.class.getName()) || cls.getName().equals(boolean.class.getName())){
            return Boolean.valueOf(cursor.getString(cursor.getColumnIndex(columnName)));
        }  else if(cls.getName().equals(String.class.getName())){
            return cursor.getString(cursor.getColumnIndex(columnName));
        }

        return null;
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
