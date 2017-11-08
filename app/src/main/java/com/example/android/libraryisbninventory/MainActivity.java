package com.example.android.libraryisbninventory;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

/**
 * This is my Library Book Inventory app
 * I jacked the Barcode Scanner(& structure) from this tutorial:
 * https://www.androidhive.info/2017/08/android-barcode-scanner-using-google-mobile-vision-building-movie-tickets-app/
 *
 * Uses ISBNdb.com as the REST service for book info
 * ISBNdb API Key = "YGKMMUIN"
 */
public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BookListDbHelper dbHelper = new BookListDbHelper(this);
        mDb = dbHelper.getReadableDatabase(); //

        Cursor cursor = getAllBooks();

        findViewById(R.id.btn_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ScanActivity.class));
            }
        });
    }

    private Cursor getAllBooks() {
        return mDb.query(
                BookListContract.BookListEntry.TABLE_NAME,
                null,null,null,null,null, null
        );
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
                startActivity(new Intent(MainActivity.this, BookInventory.class));
                return true;
            default:
                return true;
        }
    }
}
