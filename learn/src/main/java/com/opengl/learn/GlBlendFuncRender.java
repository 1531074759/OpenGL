package com.opengl.learn;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.opengl.learn.blend.Map;
import com.opengl.learn.blend.Watermark;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;

public class GlBlendFuncRender implements GLSurfaceView.Renderer {
    private static final String TAG = GlBlendFuncRender.class.getSimpleName();

    private Map mMap;
    private Watermark mWatermark;

    public GlBlendFuncRender(Context context) {
        mMap = new Map(context);
        mWatermark = new Watermark(context);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        mMap.onSurfaceCreated();
        mWatermark.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mMap.onSurfaceChanged(gl, width, height);
        mWatermark.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glBlendFunc(GL_ONE, GL_ONE);
        mMap.onDrawFrame();
        mWatermark.onDrawFrame();
    }
}
