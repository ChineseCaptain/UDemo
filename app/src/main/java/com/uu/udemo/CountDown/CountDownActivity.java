package com.uu.udemo.CountDown;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.uu.udemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CountDownActivity extends AppCompatActivity {

    CountDownTimer timer;
    @BindView(R.id.tv_index1)
    TextView tvIndex1;
    @BindView(R.id.tv_index2)
    TextView tvIndex2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_down);
        ButterKnife.bind(this);
        initTimer(tvIndex1);
        timer.start();
    }

    private void initTimer(final TextView textView) {
        timer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                textView.setText("剩余" + millisUntilFinished / 1000 + "秒");
            }

            @Override
            public void onFinish() {

            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initTimer(tvIndex2);
        timer.start();
    }
}
