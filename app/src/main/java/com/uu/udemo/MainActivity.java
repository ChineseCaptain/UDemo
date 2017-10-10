package com.uu.udemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.uu.udemo.RecyclerSnap.RecyclerSnapActivity;
import com.uu.udemo.anr.AnrActivity;
import com.uu.udemo.async.AsyncActivity;
import com.uu.udemo.loader.LoaderActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_snap, R.id.btn_anr, R.id.btn_loader, R.id.btn_async})
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
        }
    }
}
