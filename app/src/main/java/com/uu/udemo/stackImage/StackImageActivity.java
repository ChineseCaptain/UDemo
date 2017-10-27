package com.uu.udemo.stackImage;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.Toast;

import com.uu.udemo.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StackImageActivity extends AppCompatActivity implements ImageLayoutManager.LayoutCallBack {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stack_image);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        ArrayList<Integer> datas = new ArrayList<>();
        datas.add(Color.RED);
        datas.add(Color.YELLOW);
        datas.add(Color.GREEN);
        datas.add(Color.BLUE);
        datas.add(Color.BLACK);
        datas.add(Color.LTGRAY);

        SnapAdapter adapter = new SnapAdapter(this, datas);
        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager;

        // 采用不同的LayoutManager初始化
//        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager = new ImageLayoutManager(this);
//        CardItemTouchHelperCallback cardCallback = new CardItemTouchHelperCallback(recyclerView.getAdapter(), datas);
//        cardCallback.setOnSwipedListener(null);
//        final ItemTouchHelper touchHelper = new ItemTouchHelper(cardCallback);
//        touchHelper.attachToRecyclerView(recyclerView);
//        layoutManager = new CardLayoutManager(recyclerView, touchHelper);

        //设置LayoutManager
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void loadMoreData() {

    }

    @Override
    public void onItemClickListener(int index) {

    }
}
