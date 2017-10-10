package com.uu.udemo.async;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
        for (int i = 0; i < 1290; i++) {
            LoadTask task = new LoadTask();
            task.execute("开始"+i);
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
            publishProgress();
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            return result;
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
