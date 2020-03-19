package com.opengl.learn.blend;

import android.content.Context;
import android.util.Log;

import com.lime.common.ESShader;
import com.lime.common.TextureHelper;
import com.opengl.learn.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

public class Watermark {
    private static final String TAG = Watermark.class.getSimpleName();
    private static final int BYTES_PER_FLOAT = 4;
    private static final int BYTES_PER_SHORT = 2;
    private static final int POSITION_COMPONENT_SIZE = 3;
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

    private final float[] mTexturePosiontData =
            {
                    0.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f
            };

    private Context mContext;
    private int mBlendProgram;
    private int mWidth, mHeight;
    private FloatBuffer mVertices;
    private FloatBuffer mTextureBuffer;
    private ShortBuffer mIndices;
    private int aBlendPosition, aBlendTexturePosition, uBlendTextureUnit;

    private int[] mVBOIds = new int[4];
    private int mBlendTexture;

    public Watermark(Context context) {
        mContext = context;
        mVertices = ByteBuffer.allocateDirect(mVerticesData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(mVerticesData).position(0);

        mTextureBuffer = ByteBuffer.allocateDirect(mTexturePosiontData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTextureBuffer.put(mTexturePosiontData).position(0);

        mIndices = ByteBuffer.allocateDirect(mIndicesData.length * BYTES_PER_SHORT)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        mIndices.put(mIndicesData).position(0);
    }

    public void onSurfaceCreated() {
        loadWatermark();
    }

    private void loadWatermark() {
        String vShaderStr = ESShader.readShader(mContext, "blendfunc_vertexShader.glsl");
        String fShaderStr = ESShader.readShader(mContext, "blendfunc_fragmentShader.glsl");

        // Load the shaders and get a linked program object
        mBlendProgram = ESShader.loadProgram(vShaderStr, fShaderStr);

        aBlendPosition = glGetAttribLocation(mBlendProgram, "aBlendPosition");
        aBlendTexturePosition = glGetAttribLocation(mBlendProgram, "aBlendTexturePosition");
        uBlendTextureUnit = glGetUniformLocation(mBlendProgram, "uBlendTextureUnit");

        glGenBuffers(3, mVBOIds, 0);
        Log.e(TAG, "0: " + mVBOIds[0] + ", 1: " + mVBOIds[1] + ", 2: " + mVBOIds[2]);

        // mVBOIds[0] - used to store vertex position
        mVertices.position(0);
        glBindBuffer(GL_ARRAY_BUFFER, mVBOIds[0]);
        glBufferData(GL_ARRAY_BUFFER, BYTES_PER_FLOAT * mVerticesData.length,
                mVertices, GL_STATIC_DRAW);

        mTextureBuffer.position(0);
        glBindBuffer(GL_ARRAY_BUFFER, mVBOIds[1]);
        glBufferData(GL_ARRAY_BUFFER, BYTES_PER_FLOAT * mTexturePosiontData.length,
                mTextureBuffer, GL_STATIC_DRAW);

        // mVBOIds[2] - used to store element indices
        mIndices.position(0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mVBOIds[2]);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, BYTES_PER_SHORT * mIndicesData.length, mIndices, GL_STATIC_DRAW);

        mBlendTexture = TextureHelper.loadTexture(mContext, R.mipmap.watermark);
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public void onDrawFrame() {
        glViewport(0, 0, 288, 144);
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);
        drawWatermark();
        glDisable(GL_BLEND);
    }

    private void drawWatermark() {
        glUseProgram(mBlendProgram);
        glBindBuffer(GL_ARRAY_BUFFER, mVBOIds[0]);
        glEnableVertexAttribArray(aBlendPosition);
        glVertexAttribPointer(aBlendPosition, POSITION_COMPONENT_SIZE,
                GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, mVBOIds[1]);
        glEnableVertexAttribArray(aBlendTexturePosition);
        glVertexAttribPointer(aBlendTexturePosition, 2,
                GL_FLOAT, false, 0, 0);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, mBlendTexture);
        glUniform1i(mBlendTexture, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mVBOIds[2]);

        glDrawElements(GL_TRIANGLES, mIndicesData.length, GL_UNSIGNED_SHORT, 0);

        glDisableVertexAttribArray(aBlendPosition);
        glDisableVertexAttribArray(aBlendTexturePosition);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }
}