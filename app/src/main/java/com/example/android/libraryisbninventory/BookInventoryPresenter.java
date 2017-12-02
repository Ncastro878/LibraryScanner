package com.example.android.libraryisbninventory;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

/**
 * Created by nick on 12/2/2017.
 */

public class BookInventoryPresenter implements BookInventoryMVP.Presenter {


    public Cursor mainCursor;
    BookInventoryMVP.View view;
    MyAdapter mAdapter;
    /**
     * SQLiteDatabase Variables
     */
    static SQLiteDatabase mDatabase;

    public Cursor getMainCursor() {
        return mainCursor;
    }

    public BookInventoryPresenter(BookInventoryMVP.View view) {
        this.view = view;
    }

    public void initializeOnCreate() {
        BookListDbHelper dbHelper = new BookListDbHelper((BookInventoryViewActivity) view);
        mDatabase = dbHelper.getWritableDatabase();
        mainCursor = getAllBooks();
    }

    @Override
    public MyAdapter returnMyAdapter() {
        mAdapter = new MyAdapter(this);
        return mAdapter;
    }

    public Cursor getAllBooks() {
        return mDatabase.query(
                BookListContract.BookListEntry.TABLE_NAME,
                null, null, null, null, null, null);
    }

    @Override
    public void swapCursor(Cursor cursor) {
        mainCursor = cursor;
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBindHoldersViews(MyAdapter.ViewHolder holder, int position) {
        if (!mainCursor.moveToPosition(position)) {
            return;
        }
        holder.authorTextView
                .setText(mainCursor.getString(mainCursor.getColumnIndex(BookListContract.BookListEntry.BOOK_AUTHOR)));
        holder.titleTextView
                .setText(mainCursor.getString(mainCursor.getColumnIndex(BookListContract.BookListEntry.BOOK_TITLE)));
        Picasso.with(holder.authorTextView.getContext())
                .load(mainCursor.getString(mainCursor.getColumnIndex(BookListContract.BookListEntry.BOOK_IMAGE_URL)))
                .into(holder.mImageView);
    }

    @Override
    public int getDataCount() {
        return mainCursor.getCount();
    }

    public boolean removeBook(long id) {
        return mDatabase.delete(BookListContract.BookListEntry.TABLE_NAME,
                BookListContract.BookListEntry._ID + " = " + id, null) > 0;
    }

    /**
     * Making inner class of the RecyclerView.Adapter class
     */
    public static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        BookInventoryMVP.Presenter presenter;

        public MyAdapter(BookInventoryMVP.Presenter presenter) {
            this.presenter = presenter;
        }

        public class ViewHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener, BookInventoryMVP.HolderView {
            TextView authorTextView;
            TextView titleTextView;
            ImageView mImageView;
            ImageView mDeleteImage;

            ViewHolder(View itemView) {
                super(itemView);
                authorTextView = (TextView) itemView.findViewById(R.id.book_layout_author_textview);
                titleTextView = (TextView) itemView.findViewById(R.id.book_layout_title_textview);
                mImageView = (ImageView) itemView.findViewById(R.id.book_image_imageview);
                mDeleteImage = (ImageView) itemView.findViewById(R.id.delete_image_view);
                mDeleteImage.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                Cursor mCursor = presenter.getMainCursor();

                if (view.getId() == mDeleteImage.getId()) {
                    int pos = getAdapterPosition();
                    if (mCursor.moveToPosition(pos)) {
                        String title = mCursor.getString(mCursor
                                .getColumnIndex(BookListContract.BookListEntry.BOOK_TITLE));
                        long id = mCursor.getLong(mCursor.getColumnIndex(BookListContract.BookListEntry._ID));
                        if (presenter.removeBook(id)) {
                            Toast.makeText(view.getContext(),
                                    "Deleted: " + title,
                                    Toast.LENGTH_SHORT).show();
                            presenter.swapCursor(presenter.getAllBooks());
                        }
                    }
                }
            }
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_row_layout, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
            presenter.onBindHoldersViews(holder, position);
        }

        @Override
        public int getItemCount() {
            return presenter.getDataCount();
        }
    }
}
