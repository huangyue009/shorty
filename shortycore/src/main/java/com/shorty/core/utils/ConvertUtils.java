package com.shorty.core.utils;

/**
 * 转换工具
 * Created by yue.huang on 2016/6/6.
 */
public class ConvertUtils {
    public static int bytes2Int(byte[] res){
        return (res[0] & 0xff) | ((res[1] << 8) & 0xff00) // | 表示安位或
                | ((res[2] << 24) >>> 8) | (res[3] << 24);
    }

    public static int bytes2Int(byte[] res, int start){
        return (res[start] & 0xff) | ((res[start + 1] << 8) & 0xff00) // | 表示安位或
                | ((res[start + 2] << 24) >>> 8) | (res[start + 3] << 24);
    }

}
