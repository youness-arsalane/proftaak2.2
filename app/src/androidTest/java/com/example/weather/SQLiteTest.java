package com.example.weather;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.weather.dbHandler.RecentSearchesDBHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4ClassRunner.class)
public class SQLiteTest {
    private RecentSearchesDBHelper recentSearchesDBHelper;

    @Before
    public void setUp() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        recentSearchesDBHelper = new RecentSearchesDBHelper(appContext);
    }

    /**
     * Tests if SQLite database is readable
     */
    @Test
    public void SQLite_is_readable() {
        try {
            SQLiteDatabase sqLiteDatabase = recentSearchesDBHelper.getReadableDatabase();
            recentSearchesDBHelper.onCreate(sqLiteDatabase);
            recentSearchesDBHelper.getAllKeywords();

            assertTrue(true);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests if SQLite database is writable
     */
    @Test
    public void SQLite_is_writable() {
        try {
            SQLiteDatabase sqLiteDatabase = recentSearchesDBHelper.getWritableDatabase();
            recentSearchesDBHelper.onCreate(sqLiteDatabase);

            recentSearchesDBHelper.addKeyword("Test");
            assertTrue(recentSearchesDBHelper.getAllKeywords().length > 0);
            recentSearchesDBHelper.deleteAll();
            assertEquals(0, recentSearchesDBHelper.getAllKeywords().length);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @After
    public void tearDown() {
        recentSearchesDBHelper.close();
    }
}