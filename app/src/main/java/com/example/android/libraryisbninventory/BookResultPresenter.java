package com.example.android.libraryisbninventory;

/**
 * Created by nick on 12/1/2017.
 */

public class BookResultPresenter implements BookResultMVP.Presenter {
    BookResultMVP.View view;

    public BookResultPresenter(BookResultMVP.View view) {
        this.view = view;
    }
}
