package com.example.android.libraryisbninventory;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Currently using www.ISBNdb.com.
 *
 * Consider https://www.goodreads.com/api/index#search.books API
 * for book look ups.
 * Goodreads url example:
 * https://www.goodreads.com/search/index.xml?key=lA0ttkYJJOCPPiKn0JWWMQ&q=[ISBN]
 *
 */
public class BookResultActivity extends AppCompatActivity implements BookResultMVP.View{

    TextView mTextView;
    TextView mBookAuthorTextView;
    TextView mBookTitleTextView;
    ImageView mImageView;
    Button mAddBookButton;
    String currentBookImgUrl = null;

    //MVP-Presenter Variable
    BookResultMVP.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_result);

        mTextView = (TextView) findViewById(R.id.barcde_text_view);
        mBookAuthorTextView = (TextView) findViewById(R.id.book_author_text_view);
        mBookTitleTextView = (TextView) findViewById(R.id.book_title_text_view);
        mImageView = (ImageView) findViewById(R.id.image_text_view);
        mAddBookButton = (Button) findViewById(R.id.add_book_button);


        // close the activity in case of empty barcode
        String barcode = getIntent().getStringExtra("code");
        if (TextUtils.isEmpty(barcode)) {
            Toast.makeText(getApplicationContext(), "Barcode is empty!", Toast.LENGTH_LONG).show();
            finish();
        }
        mTextView.setText("The barcode read is: " + barcode);

        presenter = new BookResultPresenter(this);
        presenter.OnCreateInitialization(barcode);

        mAddBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.addBook();
                Toast.makeText(BookResultActivity.this, "Book added ! " , Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public String getBookTextViewTitle() {
        return mBookTitleTextView.getText().toString();
    }

    @Override
    public String getBookTextViewAuthor() {
        return mBookAuthorTextView.getText().toString();
    }

    @Override
    public void setBookViews(BookInfoObject book) {
        if(book != null) {
            mBookTitleTextView.setText(book.title);
            mBookAuthorTextView.setText(book.author);
            Picasso.with(this).load(book.imgUrl).resize(200,0).into(mImageView);
            //TODO: find hidden value/tag for this instead of a global value.
            currentBookImgUrl = book.imgUrl;
            Log.v("BookResultActivity", "Author info is: " + book.author);
        }
    }

    @Override
    public String getImgUrl() {
        return currentBookImgUrl;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.to_inventory_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle menu item selection
        switch(item.getItemId()){
            case R.id.inventory_menu_item:
                startActivity(new Intent(BookResultActivity.this, BookInventoryActivity.class));
                return true;
            default:
                return true;
        }
    }
}
