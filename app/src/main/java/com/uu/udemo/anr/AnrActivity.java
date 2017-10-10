package com.uu.udemo.anr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.uu.udemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AnrActivity extends AppCompatActivity {

    @BindView(R.id.edt_time)
    EditText edtTime;
    @BindView(R.id.btn_anr)
    Button btnAnr;
    @BindView(R.id.btn_start)
    Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anr);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_anr, R.id.btn_start})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_anr:
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_start:
                Toast.makeText(this, "button被点击", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
