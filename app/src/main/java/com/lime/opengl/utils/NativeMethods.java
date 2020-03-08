package com.lime.opengl.utils;

import android.hardware.HardwareBuffer;

public class NativeMethods {

    static {
        System.loadLibrary("native");
    }

    public static native void processPreview(HardwareBuffer buffer);
}
