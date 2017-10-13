package com.uu.udemo.animator;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.uu.udemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PropertyAnimatorActivity extends AppCompatActivity {

    @BindView(R.id.textView)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_animator);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.btn_animator)
    public void onViewClicked() {
        //第一步：定义多个ObjectAnimator对象
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(textView, "alpha", 1, 0);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(textView, "translationX", 100, 200, 300, 400);
        //第二步：初始化AnimatorSet对象
        AnimatorSet animatorSet = new AnimatorSet();
        //第三步：设置动画播放顺序
        animatorSet.playSequentially(animator1, animator2);
        //第四步：开始动画
        animatorSet.start();
    }
}
