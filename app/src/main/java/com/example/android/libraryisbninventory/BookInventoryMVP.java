package com.example.android.libraryisbninventory;

import android.database.Cursor;
import android.view.View;

/**
 * Created by nick on 12/2/2017.
 * This tutorial used to convert RecyclerView and Adapter into MVP format
 * https://android.jlelse.eu/recyclerview-in-mvp-passive-views-approach-8dd74633158
 */

public interface BookInventoryMVP {
    interface Presenter {
        void initializeOnCreate();

        BookInventoryPresenter.MyAdapter returnMyAdapter();

        boolean removeBook(long id);

        Cursor getAllBooks();

        void onBindHoldersViews(BookInventoryPresenter.MyAdapter.ViewHolder holder, int position);

        int getDataCount();

        void swapCursor(Cursor allBooks);

        Cursor getMainCursor();
    }

    interface View {

    }

    interface HolderView {

    }
}
