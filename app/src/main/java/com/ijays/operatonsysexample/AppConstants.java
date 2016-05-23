package com.ijays.operatonsysexample;

import android.os.Environment;

import java.io.File;

/**
 * Created by ijaysdev on 16/5/15.
 */
public class AppConstants {
    public static final int INTENT_METHOD = 0;
    public static final int SHARED_FILE_METHOD = 1;
    public static final int MESSENGER_METHOD = 2;
    public static final int CONTENT_PROVIDER_METHOD = 3;
    public static final int AIDL_METHOD = 4;
    public static final int SOCKET_METHOD=5;
    public static final int MSG_FROM_CLIENT = 0x10;
    public static final int MSG_FROM_SERVER = 0x11;
    public static final String JUMP_TYPE = "jump_type";
    public static final String PASS_DATA = "pass_data";
    public static final String ROOT_PATH = Environment.getExternalStorageDirectory().getPath();
    public static final String FILE_PATH = ROOT_PATH + "/OperationExample/";
    public static final String CACHE_FILE = FILE_PATH + "cache/";
    public static final File SCHEDULER = new File("/sys/block/mmcblk0/queue/scheduler");
}
