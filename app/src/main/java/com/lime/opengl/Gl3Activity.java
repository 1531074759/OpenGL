package com.lime.opengl;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.lime.opengl.utils.Utils;

import java.lang.reflect.Constructor;

public class Gl3Activity extends Activity {

    private static final String TAG = Gl3Activity.class.getSimpleName();
    private final int CONTEXT_CLIENT_VERSION = 3;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mGLSurfaceView = new GLSurfaceView(this);
        Intent intent = getIntent();
        String name = intent == null ? null : intent.getStringExtra(Intent.EXTRA_TEXT);
        if (TextUtils.isEmpty(name)) {
            finish();
            return;
        }
        GLSurfaceView.Renderer render = Utils.createRender(name, Gl3Activity.this);
        if (render == null) {
            finish();
            return;
        }
        if (detectOpenGLES30()) {
            // Tell the surface view we want to create an OpenGL ES 3.0-compatible
            // context, and set an OpenGL ES 3.0-compatible renderer.
            mGLSurfaceView.setEGLContextClientVersion(CONTEXT_CLIENT_VERSION);
            mGLSurfaceView.setRenderer(render);
        } else {
            Log.e(TAG, "OpenGL ES 3.0 not supported on device.  Exiting...");
            finish();
        }
        setContentView(mGLSurfaceView);
    }

    private boolean detectOpenGLES30() {
        ActivityManager am =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        return (info.reqGlEsVersion >= 0x30000);
    }

    @Override
    protected void onResume() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onPause();
        mGLSurfaceView.onPause();
    }

    private GLSurfaceView mGLSurfaceView;
}
