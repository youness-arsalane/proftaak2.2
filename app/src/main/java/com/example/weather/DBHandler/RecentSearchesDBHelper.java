package com.example.weather.DBHandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class RecentSearchesDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "recentSearches.db";
    private static final String DB_TABLE_NAME = "recent_searches";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_KEYWORD = "key_word";

    public RecentSearchesDBHelper(Context context) {
        super(context, DB_NAME, null, 5);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_RECENT_SEARCHES_TABLE = "CREATE TABLE IF NOT EXISTS " + DB_TABLE_NAME +
                "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_KEYWORD + " TEXT" +
                ")";
        db.execSQL(CREATE_RECENT_SEARCHES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_NAME);
        onCreate(db);
    }

    public void addKeyword(String keyword) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_KEYWORD, keyword);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(DB_TABLE_NAME, null, contentValues);
        db.close();
    }

    public String[] getAllKeywords() {
        String query_b = "SELECT * FROM " + DB_TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query_b, null);

        cursor.moveToFirst();
        ArrayList<String> names = new ArrayList<>();
        while(!cursor.isAfterLast()) {
            names.add(cursor.getString(cursor.getColumnIndex(COLUMN_KEYWORD)));
            cursor.moveToNext();
        }
        cursor.close();
        return names.toArray(new String[names.size()]);
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + DB_TABLE_NAME);
    }

    public void deleteDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_NAME);
    }
}