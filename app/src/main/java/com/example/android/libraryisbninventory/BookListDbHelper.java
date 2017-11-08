package com.example.android.libraryisbninventory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.R.attr.version;

/**
 * This OpenDbHelper creates and updates the SQLite database that will store the books
 *
 * Created by nick on 10/25/2017.
 */

public class BookListDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bookList.db";
    private static final int DATABASE_VERSION = 1;

    public BookListDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_TABLE_STATEMENT = "CREATE TABLE " +
                BookListContract.BookListEntry.TABLE_NAME + " (" +
                BookListContract.BookListEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                BookListContract.BookListEntry.BOOK_TITLE + " TEXT," +
                BookListContract.BookListEntry.BOOK_AUTHOR + " TEXT," +
                BookListContract.BookListEntry.BOOK_IMAGE_URL + " TEXT );";
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BookListContract.BookListEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
