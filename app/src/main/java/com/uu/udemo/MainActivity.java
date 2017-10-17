package com.uu.udemo;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.uu.udemo.RecyclerSnap.RecyclerSnapActivity;
import com.uu.udemo.animator.PropertyAnimatorActivity;
import com.uu.udemo.anr.AnrActivity;
import com.uu.udemo.async.AsyncActivity;
import com.uu.udemo.constrain.ConstrainActivity;
import com.uu.udemo.loader.LoaderActivity;
import com.uu.udemo.locale.LocaleActivity;
import com.uu.udemo.recents.RecentsActivity;
import com.uu.udemo.transition.TransitionActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_change_locale)
    Button btnChangeLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        GradientDrawable bkg = (GradientDrawable) btnChangeLocale.getBackground();
        bkg.setColor(Color.parseColor("#888888"));
    }

    @OnClick({R.id.btn_snap, R.id.btn_anr, R.id.btn_loader, R.id.btn_async, R.id.btn_change_locale,
    R.id.btn_constrain, R.id.btn_property, R.id.btn_transition, R.id.btn_mi_push, R.id.btn_recents})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_snap:
                Intent itSnap = new Intent(this, RecyclerSnapActivity.class);
                startActivity(itSnap);
                break;
            case R.id.btn_anr:
                Intent itAnr = new Intent(this, AnrActivity.class);
                startActivity(itAnr);
                break;
            case R.id.btn_loader:
                Intent itLoader = new Intent(this, LoaderActivity.class);
                startActivity(itLoader);
                break;
            case R.id.btn_async:
                Intent itAsync = new Intent(this, AsyncActivity.class);
                startActivity(itAsync);
                break;
            case R.id.btn_change_locale:
                Intent itLocale = new Intent(this, LocaleActivity.class);
                startActivity(itLocale);
                break;
            case R.id.btn_constrain:
                Intent itConstrain = new Intent(this, ConstrainActivity.class);
                startActivity(itConstrain);
                break;
            case R.id.btn_property:
                Intent itAnimator = new Intent(this, PropertyAnimatorActivity.class);
                startActivity(itAnimator);
                break;
            case R.id.btn_transition:
                Intent itTransition = new Intent(this, TransitionActivity.class);
                startActivity(itTransition);
                break;
//            case R.id.btn_mi_push:
//                Intent itMiPush = new Intent(this, MiPushActivity.class);
//                startActivity(itMiPush);
//                break;
            case R.id.btn_recents:
                Intent itMiPush = new Intent(this, RecentsActivity.class);
                startActivity(itMiPush);
                break;
        }
    }
}
