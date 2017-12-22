package com.uu.udemo.itemtouch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.uu.udemo.R;

import java.util.ArrayList;
import java.util.Collections;

public class ItemTouchActivity extends AppCompatActivity implements MyTouchCallback.OnItemTouchCallback {

    RecyclerView recyclerView;
    ItemAdapter adapter;
    ArrayList<String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_touch);
        initView();
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager;
//        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);

        data = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            data.add(i+"");
        }
        adapter = new ItemAdapter(data, this);
        recyclerView.setAdapter(adapter);

        MyTouchCallback callback = new MyTouchCallback(this);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onMove(int from, int to) {
        Collections.swap(data, from, to);
        adapter.notifyItemMoved(from, to);
    }

    @Override
    public void onSwiped(int targetPosition, int direction) {
        data.remove(targetPosition);
        adapter.notifyItemRemoved(targetPosition);
        adapter.notifyItemRangeChanged(targetPosition, data.size()-targetPosition);
    }
}
