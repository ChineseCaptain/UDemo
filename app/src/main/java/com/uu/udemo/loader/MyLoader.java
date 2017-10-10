package com.uu.udemo.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * description：
 * autohor：zhangguiyou
 * date: 2017/10/9.
 */

public class MyLoader extends AsyncTaskLoader<List<String>> {

    public MyLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<String> loadInBackground() {
        Log.i("uu", "MyLoader后台开始加载");
        ArrayList<String> result = new ArrayList<>();
        result.add("一");
        result.add("二");
        result.add("三");
        result.add("四");
        result.add("五");
        result.add("六");
        result.add("七");
        result.add("八");
        return result;
    }
}
