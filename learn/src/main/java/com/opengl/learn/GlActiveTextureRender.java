package com.opengl.learn;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.lime.common.ESShader;
import com.lime.common.TextureHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

public class GlActiveTextureRender implements GLSurfaceView.Renderer {
    private static final String TAG = GlActiveTextureRender.class.getSimpleName();
    private static final int BYTES_PER_FLOAT = 4;
    private static final int BYTES_PER_SHORT = 2;
    private static final int POSITION_COMPONENT_SIZE = 3;
    private static final int COLOR_COMPONENT_SIZE = 4;
    private final float[] mVerticesData =
            {
                    -1.0f, 0.5f, 0.0f, // v0
                    -1.0f, -0.5f, 0.0f, // v1
                    1.0f, -0.5f, 0.0f,  // v2
                    1.0f, 0.5f, 0.0f,  // v3
            };

    private final short[] mIndicesData =
            {
                    0, 1, 2,
                    0, 2, 3,
            };

    private final float[] mColorData =
            {
                    0.5f, 0.5f, 1.0f, 1.0f,   // c0
                    0.5f, 1f, 1.0f, 1.0f,   // c1
                    1.0f, 1.0f, 0.5f, 1.0f,    // c2
                    1.0f, 0.5f, 1.0f, 1.0f    // c3
            };

    private final float[] mTexturePosiontData =
            {
                    0.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f
            };

    private Context mContext;
    private int mProgramObject;
    private int mWidth, mHeight;
    private FloatBuffer mVertices;
    private FloatBuffer mColors;
    private FloatBuffer mTextureBuffer;
    private ShortBuffer mIndices;
    private int aPosition, aColor, aTexturePosition;
    private int[] mVBOIds = new int[4];
    private int mTexture;
    private int mTextureUniform;

    public GlActiveTextureRender(Context context) {
        mContext = context;
        mVertices = ByteBuffer.allocateDirect(mVerticesData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(mVerticesData).position(0);

        mColors = ByteBuffer.allocateDirect(mColorData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mColors.put(mColorData).position(0);

        mTextureBuffer = ByteBuffer.allocateDirect(mTexturePosiontData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTextureBuffer.put(mTexturePosiontData).position(0);

        mIndices = ByteBuffer.allocateDirect(mIndicesData.length * BYTES_PER_SHORT)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        mIndices.put(mIndicesData).position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        String vShaderStr = ESShader.readShader(mContext, "activetexture_vertexShader.glsl");
        String fShaderStr = ESShader.readShader(mContext, "activetexture_fragmentShader.glsl");

        // Load the shaders and get a linked program object
        mProgramObject = ESShader.loadProgram(vShaderStr, fShaderStr);

        glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        aPosition = glGetAttribLocation(mProgramObject, "aPosition");
        aColor = glGetAttribLocation(mProgramObject, "aColor");
        aTexturePosition = glGetAttribLocation(mProgramObject, "aTexturePosition");
        mTextureUniform = glGetUniformLocation(mProgramObject, "uTextureUnit");

        glGenBuffers(4, mVBOIds, 0);
        Log.e(TAG, "0: " + mVBOIds[0] + ", 1: " + mVBOIds[1] + ", 2: " + mVBOIds[2]);

        // mVBOIds[0] - used to store vertex position
        mVertices.position(0);
        glBindBuffer(GL_ARRAY_BUFFER, mVBOIds[0]);
        glBufferData(GL_ARRAY_BUFFER, BYTES_PER_FLOAT * mVerticesData.length,
                mVertices, GL_STATIC_DRAW);

        // mVBOIds[1] - used to store vertex color
        mColors.position(0);
        glBindBuffer(GL_ARRAY_BUFFER, mVBOIds[1]);
        glBufferData(GL_ARRAY_BUFFER, BYTES_PER_FLOAT * mColorData.length,
                mColors, GL_STATIC_DRAW);

        mTextureBuffer.position(0);
        glBindBuffer(GL_ARRAY_BUFFER, mVBOIds[2]);
        glBufferData(GL_ARRAY_BUFFER, BYTES_PER_FLOAT * mTexturePosiontData.length,
                mTextureBuffer, GL_STATIC_DRAW);

        // mVBOIds[2] - used to store element indices
        mIndices.position(0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mVBOIds[3]);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, BYTES_PER_SHORT * mIndicesData.length, mIndices, GL_STATIC_DRAW);

        mTexture = TextureHelper.loadTexture(mContext, R.mipmap.world);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glViewport(0, 0, mWidth, mHeight);
        glClear(GL_COLOR_BUFFER_BIT);
        glUseProgram(mProgramObject);
        drawWatermark();
    }

    private void drawWatermark() {
        glBindBuffer(GL_ARRAY_BUFFER, mVBOIds[0]);
        glEnableVertexAttribArray(aPosition);
        glVertexAttribPointer(aPosition, POSITION_COMPONENT_SIZE,
                GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, mVBOIds[1]);
        glEnableVertexAttribArray(aColor);
        glVertexAttribPointer(aColor, COLOR_COMPONENT_SIZE,
                GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, mVBOIds[2]);
        glEnableVertexAttribArray(mTexture);
        glVertexAttribPointer(mTexture, 2,
                GL_FLOAT, false, 0, 0);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, mTexture);
        glUniform1i(mTextureUniform, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mVBOIds[3]);

        glDrawElements(GL_TRIANGLES, mIndicesData.length, GL_UNSIGNED_SHORT, 0);

        glDisableVertexAttribArray(aPosition);
        glDisableVertexAttribArray(aColor);
        glDisableVertexAttribArray(mTexture);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }
}
