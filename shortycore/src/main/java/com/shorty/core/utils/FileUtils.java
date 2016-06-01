/**
 * Created with IntelliJ IDEA.
 * User: yue.huang
 * Date: 2015/4/27
 * Time: 14:35
 *
 */
package com.shorty.core.utils;

import android.os.Environment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 *
 * Created by yue.huang on 2015/4/27.
 */
public class FileUtils {


    /**
     * make dir when not exist
     */
    public static String makeDir(String dir) {
        if (!isSdcardExist()) {
            return null;
        }
        File file = new File(dir);
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                makeDir(file.getParentFile().getPath());
            }
            if (!file.mkdirs()) {
                Logger.e("--->create file dir fail!");
                return null;
            }

        }
        return dir;
    }

    /**
     * sdcard exist
     */
    public static boolean isSdcardExist() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    public static boolean makeDir(File dir) {
        if (!dir.getParentFile().exists()) {
            makeDir(dir.getParentFile());
        }
        boolean result = dir.mkdir();
        return result;
    }
}