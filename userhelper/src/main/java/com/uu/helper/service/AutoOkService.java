package com.uu.helper.service;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * description：安装应用时自动点击ok
 * author：zhangguiyou
 * date: 2017/12/22.
 */

public class AutoOkService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.i("uu", "事件类型："+event.getEventType());

        AccessibilityNodeInfo source = event.getSource();
        if (source == null) {
            return;
        }

        List<AccessibilityNodeInfo> list = source.findAccessibilityNodeInfosByText("允许");
        Log.i("uu", "寻找允许按钮:"+list.size());
        for (AccessibilityNodeInfo nodeInfo : list) {
            if(nodeInfo.isClickable()) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                Log.i("uu", "按钮被点击:"+nodeInfo.getText());

            }
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        getServiceInfo();
    }
}
