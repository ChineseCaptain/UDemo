package com.uu.udemo.async;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.uu.udemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AsyncActivity extends AppCompatActivity {

    @BindView(R.id.tv_result)
    TextView tvResult;
    @BindView(R.id.progress)
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async);
        ButterKnife.bind(this);
//        for (int i = 0; i < 129; i++) {
//            LoadTask task = new LoadTask();
//            task.execute(""+i);
//        }

        DemoTask task1 = new DemoTask();
        task1.execute(1);
        DemoTask task2 = new DemoTask();
        task2.execute(2);
    }

    class DemoTask extends AsyncTask<Integer, Integer, String> {

        @Override
        protected String doInBackground(Integer... params) {
            int num = params[0];
            try {
                Log.i("uu", "第"+num+"个任务开始执行");
                Log.i("uu", "当前线程id："+Thread.currentThread().getId());
                if (num == 1) {
                    Thread.sleep(20000);
                }else if (num == 2) {
                    Thread.sleep(1000);
                }
                Log.i("uu", "第"+num+"个任务执行结束");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class LoadTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "收到"+params[0];
            int num = Integer.parseInt(params[0]);
            Log.i("uu", num+"个线程开始执行");
            try {
                Thread.sleep(10000-num*100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publishProgress();
            Log.i("uu", num+"个线程执行结束");
            return params[0];
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            tvResult.append("\n"+s);
        }

    }
}
