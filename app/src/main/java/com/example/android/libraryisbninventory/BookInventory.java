package com.example.android.libraryisbninventory;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class BookInventory extends AppCompatActivity {

    /**
     * RecyclerView Variables
     */
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;

    /**
     * SQLiteDatabase Variables
     */
    static SQLiteDatabase mDatabase;
    Cursor mainCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_inventory);

        BookListDbHelper dbHelper = new BookListDbHelper(this);
        mDatabase = dbHelper.getWritableDatabase();

        mainCursor = getAllBooks();
        MyAdapter mAdapter = new MyAdapter(mainCursor);
        mRecyclerView = (RecyclerView) findViewById(R.id.book_recyclerview);
        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(mAdapter);
    }

    private static Cursor getAllBooks() {
        return mDatabase.query(BookListContract.BookListEntry.TABLE_NAME,
                null, null, null, null, null, null);
    }

    /**
     * Making inner class of the RecyclerView.Adapter class
     */
    public static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
        static Cursor mCursor;

        public MyAdapter(Cursor cursor){
            mCursor = cursor;
        }
        public void swapCursor(Cursor cursor){
            mCursor = cursor;
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
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
                if(view.getId() == mDeleteImage.getId()){
                    int pos = getAdapterPosition();
                    String title = "";
                    if (MyAdapter.mCursor.moveToPosition(pos)) {
                        title = MyAdapter.mCursor
                                .getString(mCursor.getColumnIndex(BookListContract.BookListEntry.BOOK_TITLE));
                        long id = mCursor.getLong(mCursor.getColumnIndex(BookListContract.BookListEntry._ID));
                        if(BookInventory.removeBook(id)){
                            Toast.makeText(view.getContext(),
                                    "Deleted: " + title ,
                                    Toast.LENGTH_SHORT).show();
                            swapCursor(BookInventory.getAllBooks());
                        };
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
            if(!mCursor.moveToPosition(position)){
                return;
            }
            holder.authorTextView
                    .setText(mCursor.getString(mCursor.getColumnIndex(BookListContract.BookListEntry.BOOK_AUTHOR)));
            holder.titleTextView
                    .setText(mCursor.getString(mCursor.getColumnIndex(BookListContract.BookListEntry.BOOK_TITLE)));
            // TODO: 10/26/2017  load image into holder imageview
            Picasso.with(holder.authorTextView.getContext())
                    .load(mCursor.getString(mCursor.getColumnIndex(BookListContract.BookListEntry.BOOK_IMAGE_URL)))
                    .into(holder.mImageView);
        }

        @Override
        public int getItemCount() {
            return mCursor.getCount();
        }
    }

    private static boolean removeBook(long id) {
        return mDatabase.delete(BookListContract.BookListEntry.TABLE_NAME,
                BookListContract.BookListEntry._ID + " = " + id,null) > 0;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.to_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle menu item selection
        switch(item.getItemId()){
            case R.id.scanner_menu_item:
                startActivity(new Intent(BookInventory.this, ScanActivity.class));
                return true;
            case R.id.main_screen_item:
                startActivity(new Intent(BookInventory.this, MainActivity.class));
            default:
                return true;
        }
    }
}
