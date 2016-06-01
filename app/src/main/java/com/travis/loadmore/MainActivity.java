package com.travis.loadmore;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements MyAdapter.DynamicLoadListener {
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private MyAdapter<String> mMyAdapter;
    private ArrayList<String> datas = new ArrayList<>();

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mMyAdapter = new MyAdapter<>(this, datas, this);
        mRecyclerView.setAdapter(mMyAdapter);
    }


    private int counter = 0;

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (counter < 3) {
                    for (int i = 0; i < 10; i++) {
                        datas.add("");
                    }
                    counter++;
                }
                mMyAdapter.updateState(counter < 3);
            }
        }, 2000L);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_reset) {
            counter = 0;
            datas.clear();
            mMyAdapter.updateState(true);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
