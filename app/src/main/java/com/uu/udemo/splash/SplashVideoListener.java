package com.uu.udemo.splash;

import android.media.MediaPlayer;

/**
 * description：
 * autohor：zhangguiyou
 * date: 2017/11/8.
 */

public interface SplashVideoListener {

    void onPrepared(MediaPlayer mp);
    void onErrorListener(int what, int extra);
    void onCompletedListener();
}
