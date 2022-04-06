package com.example.mydingdong.service;

import android.accessibilityservice.AccessibilityService;

import android.nfc.Tag;
import android.provider.Settings;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.annotation.RestrictTo;


import java.util.List;


/**
 * @author: ming
 * @date: 2022/4/5 19:43
 * https://www.jianshu.com/p/e2025c317e1f  --------- 理解教程
 */
public class ClickHelper extends AccessibilityService {


    private String currentClassName = "";
    private boolean chooseTimeSuccess = false;
    private boolean enableJumpCart = true;
    private Integer checkNotificationCount = 0;
    private String tag = "DingDongService";


    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (accessibilityEvent != null) {
            handleClassName(accessibilityEvent);
            switch (currentClassName) {
                case ClassName.CART_ACTIVITY: {
                    pay(accessibilityEvent);
                    break;
                }
                case ClassName.HOME_ACTIVITY: {
                    jumpToCartActivity(accessibilityEvent);
                    break;
                }
                case ClassName.CHOOSE_DELIVERY_TIME: {
                    chooseDeliveryTime(accessibilityEvent);
                    break;
                }
                case ClassName.GX0: {
                    performGlobalAction(GLOBAL_ACTION_BACK);
                    break;
                }
                case ClassName.XN1: {
                    checkNotification(accessibilityEvent);
                    break;
                }
                case ClassName.RETURN_CART_DIALOG: {
                    clickReturnCartBtn(accessibilityEvent);
                    break;
                }
                default: {
                    clickDialog(accessibilityEvent);
                }
            }
        }
    }

    private void chooseDeliveryTime(AccessibilityEvent accessibilityEvent) {
        AccessibilityNodeInfo source = accessibilityEvent.getSource();
        if (source != null) {
            List<AccessibilityNodeInfo> nodeInfos = source.findAccessibilityNodeInfosByText("-");
            if (nodeInfos != null) {
                for (AccessibilityNodeInfo nodeInfo : nodeInfos) {
                    if (nodeInfo.getParent().isEnabled()) {
                        chooseTimeSuccess = true;
                        nodeInfo.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        break;
                    }
                }
            }
        }
        if (!chooseTimeSuccess){
            performGlobalAction(GLOBAL_ACTION_BACK);
        }

    }

    private void clickDialog(AccessibilityEvent accessibilityEvent) {
        AccessibilityNodeInfo source = accessibilityEvent.getSource();
        if (source != null) {
            List<AccessibilityNodeInfo> nodeInfos = source.findAccessibilityNodeInfosByText("继续支付");
            if (nodeInfos == null) {
                nodeInfos = source.findAccessibilityNodeInfosByText("修改送达时间");
            }
            if (nodeInfos != null) {
                for (AccessibilityNodeInfo nodeInfo : nodeInfos) {
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    break;
                }
            }
        }

    }

    private void clickReturnCartBtn(AccessibilityEvent accessibilityEvent) {

        AccessibilityNodeInfo source = accessibilityEvent.getSource();
        if (source != null) {
            List<AccessibilityNodeInfo> nodeInfos = source.findAccessibilityNodeInfosByText("返回购物车");
            if (nodeInfos != null) {
                for (AccessibilityNodeInfo nodeInfo : nodeInfos) {
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    break;
                }
            }
        }
    }

    // 不太理解
    private void checkNotification(AccessibilityEvent accessibilityEvent) {
        if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            if (checkNotificationCount++ > 1) {
                performGlobalAction(GLOBAL_ACTION_BACK);
            }
        } else {
            checkNotificationCount = 0;
        }
    }

    private void jumpToCartActivity(AccessibilityEvent accessibilityEvent) {
        if (!enableJumpCart) {
            return;
        }
        AccessibilityNodeInfo source = accessibilityEvent.getSource();
        if (source != null) {
            List<AccessibilityNodeInfo> nodeInfos = source.findAccessibilityNodeInfosByText("去结算");
            if (nodeInfos != null) {
                for (AccessibilityNodeInfo nodeInfo : nodeInfos) {
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    enableJumpCart = false;
                    break;
                }
            }
        }
    }

    private void pay(AccessibilityEvent accessibilityEvent) {
        AccessibilityNodeInfo source = accessibilityEvent.getSource();
        if (source != null) {
            List<AccessibilityNodeInfo> nodeInfos = source.findAccessibilityNodeInfosByText("立即支付");
            if (nodeInfos != null) {
                for (AccessibilityNodeInfo node : nodeInfos) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        }
    }


    private void handleClassName(AccessibilityEvent accessibilityEvent) {
        if (accessibilityEvent.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return;
        }
        currentClassName = (String) accessibilityEvent.getClassName();
        if (ClassName.CART_ACTIVITY.equals(currentClassName) || ClassName.CHOOSE_DELIVERY_TIME.equals(currentClassName)) {
            enableJumpCart = true;
        }
        if (ClassName.HOME_ACTIVITY.equals(currentClassName)) {
            checkNotificationCount = 0;
        }
    }


    @Override
    public void onInterrupt() {

    }
}
