package com.uu.udemo.splash;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uu.udemo.MainActivity;
import com.uu.udemo.R;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SplashActivity extends AppCompatActivity implements SplashVideoListener {

    private static final String TAG = "SplashActivity";

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @BindView(R.id.image_ad)
    ImageView imageview;
//    @BindView(R.id.video_ad)
//    VideoView mVideoView;
//    @BindView(R.id.video_ad)
//    SplashTextureView mVideoView;
//    @BindView(R.id.video_ad)
//    SplashVideoView mVideoView;
    @BindView(R.id.video_ad)
    SplashSurfaceView mVideoView;
    @BindView(R.id.tv_hint)
    TextView tvHint;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.layout_skip)
    LinearLayout layoutSkip;

    boolean hasGoMain = false;
    private CountDownTimer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去掉标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去掉Activity上面的状态栏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        mVideoView.setSplashListener(this);
        verifyStoragePermissions(this);
    }

    private void playVideo() {
        String path;
//        path = Environment.getExternalStorageDirectory()+"/DCIM/100MEDIA/VIDEO0003.mp4";
//        path = Environment.getExternalStorageDirectory() + "/DCIM/100MEDIA/VIDEO0004.mp4";
        path = "android.resource://" + getPackageName() + "/" + R.raw.splash;
        Log.i("uu", "视频路径：" + path);
        File file = new File(path);
        if (file != null) {
            mVideoView.setVideoPath(path);
            mVideoView.start();
            tvHint.setText("跳过");
        }

        mVideoView.setVisibility(View.VISIBLE);
    }


    /**
     * 检查应用程序是否允许写入存储设备
     * 如果应用程序不允许那么会提示用户授予权限
     *
     * @param activity
     */
    public void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);


        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("uu", "没有读写存储的权限");
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {
            Log.i("uu", "拥有读写存储的权限");
            playVideo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                Log.i("uu", "成功获取读写存储的权限");
                playVideo();
                break;
        }
    }

    @OnClick({R.id.layout_skip, R.id.video_ad})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_skip:
                Log.i(TAG, "跳过");
                goToMain();
                break;
            case R.id.video_ad:
                Toast.makeText(this, "跳转到广告页", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.stop();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        int videoDuration = mVideoView.getMediaPlayer().getDuration();
        tvTime.setText(videoDuration+"秒");
        mTimer = new CountDownTimer(videoDuration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTime.setText(millisUntilFinished/1000+"秒");
            }

            @Override
            public void onFinish() {
                onCompletedListener();
            }
        };
        mTimer.start();
    }

    @Override
    public void onErrorListener(int what, int extra) {
        Log.i(TAG, "播放错误");
        goToMain();
    }

    @Override
    public void onCompletedListener() {
        Log.i(TAG, "播放完成");
        goToMain();
    }

    private void goToMain() {
        if (hasGoMain) {
            return;
        }
        hasGoMain = true;
        Intent itMain = new Intent();
        itMain.setClass(this, MainActivity.class);
        startActivity(itMain);
        finish();
    }


}
