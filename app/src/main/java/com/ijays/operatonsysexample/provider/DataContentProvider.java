package com.ijays.operatonsysexample.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by ijaysdev on 16/5/18.
 */
public class DataContentProvider extends ContentProvider {
    private static final int URI_CODE = 1;
    private static final String AUTHORITY = "com.ijays.operatonsysexample.provider";
    public static final Uri PASS_DATA_URI = Uri.parse("content://" + AUTHORITY + "/pass");

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AUTHORITY, "pass", URI_CODE);
    }

    private SQLiteDatabase mDb;

    @Override
    public boolean onCreate() {
        mDb = new PassDataDbHelper(getContext()).getWritableDatabase();
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        String table = getTableName(uri);
        if (table == null) {
            throw new IllegalArgumentException("Excepted URI: " + uri);
        }

        return mDb.query(table, projection, selection, selectionArgs,null,null, sortOrder, null);
    }

    /**
     * 返回一个URI请求对应的MIME
     *
     * @param uri
     * @return
     */
    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    private String getTableName(Uri uri) {
        String tableName = null;
        switch (sUriMatcher.match(uri)) {
            case URI_CODE:
                tableName = PassDataDbHelper.PASS_TABLE_NAME;
                break;
            default:
                break;
        }
        return tableName;
    }
}
