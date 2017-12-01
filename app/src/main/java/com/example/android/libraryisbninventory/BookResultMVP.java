package com.example.android.libraryisbninventory;

/**
 * Created by nick on 12/1/2017.
 */

public interface BookResultMVP {

    interface Presenter{
        void OnCreateInitialization(String barcode);
        void addBook();
    }

    interface View{
        void setBookViews(BookInfoObject bookInfo);
        String getBookTextViewTitle();
        String getBookTextViewAuthor();
        String getImgUrl();
    }
}
