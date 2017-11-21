package com.uu.udemo.autoOk;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

/**
 * description：
 * autohor：zhangguiyou
 * date: 2017/11/21.
 */

public class ClickService extends AccessibilityService {

    private static final String TAG = "ClickService";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        if(eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            Log.i(TAG, "type: TYPE_WINDOW_STATE_CHANGED");
        } else if(eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            Log.i(TAG, "type: TYPE_WINDOW_CONTENT_CHANGED");
        }
    }

    @Override
    public void onInterrupt() {
        Toast.makeText(this, "服务已经中断", Toast.LENGTH_SHORT).show();
    }
}
