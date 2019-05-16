package com.jianjin.camera;

import com.jianjin.camera.widget.CameraManager;

/**
 * Created by Administrator on 2018/5/9.
 */
public enum FlashLightStatus {
    // 只有开和关状态，可以添加一个自动状态
    LIGHT_ON, LIGHT_OFF;//, LIGHT_AUTO;

    public FlashLightStatus next() {
        int index = ordinal();
        int len = FlashLightStatus.values().length;
        FlashLightStatus status = FlashLightStatus.values()[(index + 1) % len];
        if (CameraManager.mFlashLightNotSupport.contains(status.name())) {
            return next();
        } else {
            return status;
        }
    }

    public static FlashLightStatus valueOf(int index) {
        return FlashLightStatus.values()[index];
    }
}
