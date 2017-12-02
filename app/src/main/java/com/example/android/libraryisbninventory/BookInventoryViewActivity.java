package com.example.android.libraryisbninventory;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class BookInventoryViewActivity extends AppCompatActivity implements BookInventoryMVP.View{

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    BookInventoryMVP.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_inventory);

        presenter = new BookInventoryPresenter(this);
        presenter.initializeOnCreate();

        BookInventoryPresenter.MyAdapter mAdapter = presenter.returnMyAdapter();
        mRecyclerView = (RecyclerView) findViewById(R.id.book_recyclerview);
        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
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
                startActivity(new Intent(BookInventoryViewActivity.this, ScanActivity.class));
                return true;
            case R.id.main_screen_item:
                startActivity(new Intent(BookInventoryViewActivity.this, MainActivity.class));
            default:
                return true;
        }
    }
}
