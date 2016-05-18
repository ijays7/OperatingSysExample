package com.ijays.operatonsysexample.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ijaysdev on 16/5/18.
 */
public class PassDataDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "data.db";
    public static final String PASS_TABLE_NAME = "pass_content";
    private static final int DB_VERSION = 1;

    private String CREATE_DATA_TABLE = "CREATE TABLE IF NOT EXISTS "
            + PASS_TABLE_NAME
            + "(_id INTEGER PRIMARY KEY AUTOINCREMENT," + "content TEXT)";

    public PassDataDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_DATA_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
