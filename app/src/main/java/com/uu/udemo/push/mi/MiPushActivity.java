package com.uu.udemo.push.mi;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.uu.udemo.R;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MiPushActivity extends AppCompatActivity {

    public static final String TAG = "MiPushActivity";

    // user your appid the key.
    private static final String APP_ID = "2882303761517625719";
    // user your appid the key.
    private static final String APP_KEY = "5941762533719";
    // 给后台用
    private static final String APP_SECRET = "sGrB7EiXaHCewJbc6xfIUQ==";

    public static List<String> logList = new CopyOnWriteArrayList<String>();

    private static MiPushHandler mHandler;

    @BindView(R.id.tv_result)
    TextView mLogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_push);
        ButterKnife.bind(this);
        mHandler = new MiPushHandler(this);
    }

    @OnClick({R.id.btn_start, R.id.btn_pause, R.id.btn_recover, R.id.btn_stop})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                startPush();
                break;
            case R.id.btn_pause:
                MiPushClient.pausePush(MiPushActivity.this, null);
                break;
            case R.id.btn_recover:
                MiPushClient.resumePush(MiPushActivity.this, null);
                break;
            case R.id.btn_stop:
                break;
        }
    }

    private void startPush() {
        // 注册push服务，注册成功后会向DemoMessageReceiver发送广播
        // 可以从DemoMessageReceiver的onCommandResult方法中MiPushCommandMessage对象参数中获取注册信息
        if (shouldInit()) {
            MiPushClient.registerPush(this, APP_ID, APP_KEY);
        }
        LoggerInterface newLogger = new LoggerInterface() {

            @Override
            public void setTag(String tag) {
                // ignore
            }

            @Override
            public void log(String content, Throwable t) {
                Log.d(TAG, content, t);
            }

            @Override
            public void log(String content) {
                Log.d(TAG, content);
            }
        };
        Logger.setLogger(this, newLogger);
    }

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshLogInfo();
    }

    public void refreshLogInfo() {
        String AllLog = "";
        for (String log : logList) {
            AllLog = AllLog + log + "\n\n";
        }
        mLogView.setText(AllLog);
    }

    public static MiPushHandler getHandler() {
        return mHandler;
    }

    public class MiPushHandler extends Handler {

        private Context context;

        public MiPushHandler(Context context) {
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            String s = (String) msg.obj;
            refreshLogInfo();
            if (!TextUtils.isEmpty(s)) {
                Toast.makeText(context, s, Toast.LENGTH_LONG).show();
            }
        }
    }
}
