package com.uu.udemo.match;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.uu.udemo.R;
import com.uu.udemo.RecyclerSnap.SnapAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 新版缘分页
 */
public class MatchActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_snap);
        ButterKnife.bind(this);
        initRecyclerView();
    }


    private void initRecyclerView() {
        ArrayList<String> datas = new ArrayList<>();
        datas.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=4253364667,2788064886&fm=27&gp=0.jpg");
        datas.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2407254976,468720299&fm=27&gp=0.jpg");
        datas.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2485498041,1848404660&fm=27&gp=0.jpg");
        datas.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1340136656,28327988&fm=27&gp=0.jpg");
        datas.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1301862478,2558390359&fm=11&gp=0.jpg");
        datas.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2217980408,978627880&fm=27&gp=0.jpg");

        MatchAdapter adapter = new MatchAdapter(this, datas);
        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager;
//        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager = new MatchLayoutManager(null);
        recyclerView.setLayoutManager(layoutManager);

        MatchSnapHelper snapHelper = new MatchSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
    }
}
