package com.ijays.operatonsysexample;

import android.os.Environment;

/**
 * Created by ijaysdev on 16/5/15.
 */
public class AppConstants {
    public static final int INTENT_METHOD = 0;
    public static final int SHARED_FILE_METHOD = 1;
    public static final String JUMP_TYPE = "jump_type";
    public static final String ROOT_PATH = Environment.getExternalStorageDirectory().getPath();
    public static final String FILE_PATH = ROOT_PATH + "/OperationExample/";
    public static final String CACHE_FILE = FILE_PATH + "cache/";
}
