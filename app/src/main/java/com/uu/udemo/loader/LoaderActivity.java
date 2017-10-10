package com.uu.udemo.loader;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.uu.udemo.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LoaderActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<String>> {

    @BindView(R.id.tv_result)
    TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);
        ButterKnife.bind(this);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<List<String>> onCreateLoader(int id, Bundle args) {
        Log.i("uu", "开始初始化Loader");
        return new MyLoader(LoaderActivity.this);
    }

    @Override
    public void onLoadFinished(Loader<List<String>> loader, List<String> data) {
        Log.i("uu", "加载结束");
        for (String s : data) {
            tvResult.append(s);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<String>> loader) {

    }
}
