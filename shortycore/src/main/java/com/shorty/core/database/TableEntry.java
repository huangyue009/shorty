package com.shorty.core.database;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 描述表格参数的实体类
 * Created by yue.huang on 16/6/24.
 */
class TableEntry {
    String tableName;
    HashMap<String, String> colunmName = new HashMap<String, String>();
    boolean unioue = false;
    String unioueColunmName;
}
