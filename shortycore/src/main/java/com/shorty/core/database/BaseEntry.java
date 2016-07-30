package com.shorty.core.database;

import com.shorty.core.annotation.DatabaseField;

import java.io.Serializable;

/**
 * database entry base class
 * Created by yue.huang on 2016/4/14.
 */
public class BaseEntry implements Serializable{
    public final static String COLUMN_KEY_ID = "_id";

    @DatabaseField(columnName = COLUMN_KEY_ID, isPrimary = true)
    public Integer id;
}
