package com.shorty.core.database;

import com.shorty.core.annotation.DatabaseField;
import com.shorty.core.annotation.DatabaseTable;

/**
 * test table control
 * Created by yue.huang on 2016/4/14.
 */
@DatabaseTable(tableName = "test_table")
public class TestTableEntry extends BaseEntry {
    @DatabaseField
    public Integer eid;
    @DatabaseField
    public Long longID;
    @DatabaseField(canBeNull = false)
    public String stringID;
    @DatabaseField(columnName = "bool_id")
    public Boolean boolId;
}
