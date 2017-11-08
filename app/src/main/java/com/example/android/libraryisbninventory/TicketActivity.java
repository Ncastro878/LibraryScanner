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
public class TicketActivity extends AppCompatActivity {

    final String ISBN_DB_KEY = "YGKMMUIN";
    private SQLiteDatabase mDb;

    TextView mTextView;
    TextView mBookAuthorTextView;
    TextView mBookTitleTextView;
    ImageView mImageView;
    Button mAddBookButton;

    String currentBookImgUrl;

    public TicketActivity() throws MalformedURLException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        mTextView = (TextView) findViewById(R.id.barcde_text_view);
        mBookAuthorTextView = (TextView) findViewById(R.id.book_author_text_view);
        mBookTitleTextView = (TextView) findViewById(R.id.book_title_text_view);
        mImageView = (ImageView) findViewById(R.id.image_text_view);
        mAddBookButton = (Button) findViewById(R.id.add_book_button);

        BookListDbHelper dbHelper = new BookListDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        String barcode = getIntent().getStringExtra("code");

        // close the activity in case of empty barcode
        if (TextUtils.isEmpty(barcode)) {
            Toast.makeText(getApplicationContext(), "Barcode is empty!", Toast.LENGTH_LONG).show();
            finish();
        }
        mTextView.setText("The barcode read is: " + barcode);
        new MyAsyncTask().execute(barcode);

        mAddBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewBook();
                Toast.makeText(TicketActivity.this, "Book added ! " , Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addNewBook() {
        ContentValues cv = new ContentValues();
        cv.put(BookListContract.BookListEntry.BOOK_AUTHOR, mBookAuthorTextView.getText().toString());
        cv.put(BookListContract.BookListEntry.BOOK_TITLE, mBookTitleTextView.getText().toString());
        cv.put(BookListContract.BookListEntry.BOOK_IMAGE_URL, currentBookImgUrl );
        Long num = mDb.insert(BookListContract.BookListEntry.TABLE_NAME, null, cv);
        Log.v("TicketActivity.java", "Book inserted. num value is: " + num);
    }

    private String retrieveBookInfo(String barcode)  {
        //Open connection to ISBNdb
        //returns a stream of book info in JSON
        InputStream stream = null;
        String result = null;
        try {
            String url = buildUrl(barcode);
            URL isbnUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) isbnUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int status = connection.getResponseCode();
            Log.d("retrieveBookInfo", "Status code is: " + status);
            if(status != HttpURLConnection.HTTP_OK){
                throw new IOException("HTTP code error: " + status);
            }
            stream = connection.getInputStream();
            if(stream != null){
                result = readStream(stream);
            }
        }catch (IOException e){
            Log.e("TicketActivity.this", "Error Detected: " + e);
        }
        return result;
    }

    private String readStream(InputStream stream)
            throws IOException{
        StringBuffer sb = new StringBuffer();
        String inputLine = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        while((inputLine = br.readLine()) != null){
            sb.append(inputLine);
        }
        return sb.toString();
    }

    private String buildUrl(String isbn) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("www.isbndb.com")
                .appendPath("api")
                .appendPath("v2")
                .appendPath("json")
                .appendPath(ISBN_DB_KEY)
                .appendPath("book")
                .appendPath(isbn);
        String newUrl = builder.build().toString();
        return newUrl;
    }

    public void setBookViews(BookInfo book){
        if(book != null) {
            mBookTitleTextView.setText(book.title);
            mBookAuthorTextView.setText(book.author);
            Picasso.with(this).load(book.imgUrl).resize(200,0).into(mImageView);
            currentBookImgUrl = book.imgUrl;
            Log.v("TicketActivity", "Author info is: " + book.author);
        }
    }

    private String getAuthorFromJson(String bookInfo) {
        String author = null;
        try {
            JSONObject json = new JSONObject(bookInfo);
            JSONArray jsonArray = json.getJSONArray("data");
            JSONObject dataJson = jsonArray.getJSONObject(0);
            JSONArray authorJson = dataJson.getJSONArray("author_data");
            JSONObject authorObject = authorJson.getJSONObject(0);
            String id = authorObject.getString("id");
            String name = authorObject.getString("name");
            return name;
        }catch (JSONException e){
            Log.e("TicketActivity.this", "Error caught: " + e);
        }
        return null;
    }

    public class MyAsyncTask extends AsyncTask<String, Void, BookInfo>{

        @Override
        protected BookInfo doInBackground(String... urls) {
            //String bookInfoStream = retrieveBookInfo(urls[0]);
            String xmlDocument = retrieveGoodReadsBookInfo(urls[0]);
            Log.v("TicketActivity.java", "XMlDoc is: " + xmlDocument);
            //String author = getAuthorFromJson(bookInfoStream);
            //Log.d("MyAsynctask", "BookInfoStream is:" + bookInfoStream);
            Log.d("MyAsyncTask", "DoinBackground done");
            //Log.d("MyAsyncTask", "author info is: " + author);
            //return author;
            BookInfo book = null;
            try {
                book = parseXml(xmlDocument);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return book;
        }

        @Override
        protected void onPostExecute(BookInfo bookInfo) {
            super.onPostExecute(bookInfo);
            //mBookInfoTextView.setText(s);
            TicketActivity.this.setBookViews(bookInfo);
        }
    }

    private String retrieveGoodReadsBookInfo(String barcode) {
        //Open connection to ISBNdb
        //returns a stream of book info in JSON
        InputStream stream = null;
        String result = null;
        try {
            String url = buildGoodReadsUrl(barcode);
            URL isbnUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) isbnUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int status = connection.getResponseCode();
            Log.d("retrieveBookInfo", "Status code is: " + status);
            if(status != HttpURLConnection.HTTP_OK){
                throw new IOException("HTTP code error: " + status);
            }
            stream = connection.getInputStream();
            if(stream != null){
                result = readStream(stream);
            }
        }catch (IOException e){
            Log.e("TicketActivity.this", "Error Detected: " + e);
        }
        return result;
    }

    private String buildGoodReadsUrl(String isbn) {
        String goodReadsKey = "lA0ttkYJJOCPPiKn0JWWMQ";
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("www.goodreads.com")
                .appendPath("search")
                .appendPath("index.xml")
                .appendQueryParameter("key", goodReadsKey)
                .appendQueryParameter("q",isbn);
        String newUrl = builder.build().toString();
        return newUrl;
    }

    /**
     * This tutorial was used as guidance:
     * https://www.sitepoint.com/learning-to-parse-xml-data-in-your-android-app/
     * (as was android documentation)
     * @param xmlDocument
     * @return BookInfo Oject
     * @throws XmlPullParserException
     * @throws IOException
     */
    private BookInfo parseXml(String xmlDocument) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(new StringReader(xmlDocument));
        Log.v("TicketActivity.java", "Enacting parseXml().");
        int eventType = parser.getEventType();
        BookInfo newBook = new BookInfo();
        while(eventType != XmlPullParser.END_DOCUMENT){
            String name = null;
            switch(eventType){
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    Log.v("TicketActivity.java","Event name is: " + name);
                    if(name .equals( "title")){
                        newBook.title = parser.nextText();
                        Log.v("TicketActivity.java","Book title is: " + newBook.title);
                    }else if(name.equals("name")){
                        newBook.author = parser.nextText();
                        Log.v("TicketActivity.java","Book author is: " + newBook.author);
                    }else if(name.equals("image_url")){
                        newBook.imgUrl = parser.nextText();
                        Log.v("TicketActivity.java","imgurl   is: " + newBook.imgUrl);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    break;
            }
            eventType = parser.next();
        }
        Log.v("TicketActivity.java", newBook.author + " is the author.");
        Log.v("TicketActivity.java", newBook.title + " is the title.");
        return newBook;
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
                startActivity(new Intent(TicketActivity.this, BookInventory.class));
                return true;
            default:
                return true;
        }
    }
}
