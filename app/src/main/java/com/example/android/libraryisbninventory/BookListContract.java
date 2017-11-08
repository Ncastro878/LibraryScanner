package com.example.android.libraryisbninventory;

import android.provider.BaseColumns;

/**
 * The contract for the SqLite database.
 * Created by nick on 10/25/2017.
 */

public class BookListContract {

    public static final class BookListEntry implements BaseColumns{
        public static final String TABLE_NAME = "bookTable";
        public static final String BOOK_TITLE = "bookTitle";
        public static final String BOOK_AUTHOR = "bookAuthor";
        public static final String BOOK_IMAGE_URL = "bookImageUrl";
    }
}
