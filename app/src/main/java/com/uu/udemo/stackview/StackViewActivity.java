package com.uu.udemo.stackview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.StackView;
import android.widget.TextView;

import com.uu.udemo.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StackViewActivity extends AppCompatActivity {

    StackAdapter adapter;

    @BindView(R.id.tv_info)
    TextView tvInfo;
    @BindView(R.id.stack_view)
    StackView stackView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stack_view);
        ButterKnife.bind(this);
        init();
    }

    private void init() {

        ArrayList<String> datas = new ArrayList<>();
        datas.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=1516891756,2194615571&fm=27&gp=0.jpg");
        datas.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=2639088341,2223755776&fm=27&gp=0.jpg");
        datas.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=2856723681,2411189826&fm=27&gp=0.jpg");
        datas.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1602552054,373587514&fm=27&gp=0.jpg");
        datas.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=2139391670,4174270048&fm=27&gp=0.jpg");
        datas.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2921208321,2605531147&fm=27&gp=0.jpg");
        datas.add("http://img.ivsky.com/img/tupian/li/201707/30/huangwurenyandeshamotupian.jpg");
        datas.add("http://img.ivsky.com/img/tupian/li/201707/30/qitedeyanshitupian-006.jpg");
        datas.add("http://img.ivsky.com/img/tupian/li/201707/28/wuqiliaoraodesenlintupian.jpg");

        adapter = new StackAdapter(this, datas);
        stackView.setAdapter(adapter);
    }

}
