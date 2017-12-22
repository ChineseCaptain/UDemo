package com.uu.helper.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * description：自动点击跳过的Service
 * author：zhangguiyou
 * date: 2017/12/21.
 */

public class SkipService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.i("uu", "事件类型:"+event.getEventType());
        AccessibilityNodeInfo source = event.getSource();
        if (source == null) {
            return;
        }

        List<AccessibilityNodeInfo> list = source.findAccessibilityNodeInfosByText("跳过");
        Log.i("uu", "寻找跳过按钮:"+list.size());
        for (AccessibilityNodeInfo nodeInfo : list) {
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            Log.i("uu", "按钮被点击:"+nodeInfo.getText());
//            if(nodeInfo.isClickable()) {
//                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                Log.i("uu", "按钮被点击:"+nodeInfo.getText());
//            }
        }

    }

    @Override
    public void onInterrupt() {

    }
}
