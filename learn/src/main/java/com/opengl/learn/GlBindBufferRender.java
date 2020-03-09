package com.opengl.learn;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.lime.common.ESShader;

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
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

public class GlBindBufferRender implements GLSurfaceView.Renderer {
    private static final String TAG = GlBindBufferRender.class.getSimpleName();
    private Context mContext;
    private int mProgramObject;

    private static final int BYTES_PER_FLOAT = 4;
    private static final int BYTES_PER_SHORT = 2;
    private static final int POSITION_COMPONENT_SIZE = 3;
    private static final int COLOR_COMPONENT_SIZE = 4;
    private static final int VERTICES_COUNT = 6;

    // Additional member variables
    private int mWidth;
    private int mHeight;
    private FloatBuffer mVertices;
    private FloatBuffer mColors;
    private ShortBuffer mIndices;
    private int vPosition, vColor;

    private final float[] mVerticesData =
            {
                    0.0f, 0.0f, 0.0f, // v0
                    0.0f, 0.5f, 0.0f, // v0
                    -0.5f, 0.0f, 0.0f, // v1
                    -0.5f, -0.5f, 0.0f,  // v2
                    0.5f, -0.5f, 0.0f,  // v2
                    0.5f, 0.0f, 0.0f  // v2
            };

    private final short[] mIndicesData =
            {
                    0, 1, 2,
                    0, 2, 3,
                    0, 3, 4,
                    0, 4, 5,
                    0, 5, 1
            };

    private final float[] mColorData =
            {
                    1.0f, 0.0f, 0.0f, 1.0f,   // c0
                    0.0f, 1.0f, 0.0f, 1.0f,   // c1
//                    0.0f, 0.0f, 1.0f, 1.0f,    // c2
//                    0.0f, 1.0f, 0.0f, 1.0f,   // c0
                    1.0f, 0.0f, 0.0f, 1.0f,   // c1
                    0.0f, 1.0f, 0.0f, 1.0f    // c2
            };

    // VertexBufferObject Ids
    private int[] mVBOIds = new int[3];

    public GlBindBufferRender(Context context) {
        mContext = context;
        mVertices = ByteBuffer.allocateDirect(mVerticesData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(mVerticesData).position(0);

        mColors = ByteBuffer.allocateDirect(mColorData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mColors.put(mColorData).position(0);

        mIndices = ByteBuffer.allocateDirect(mIndicesData.length * BYTES_PER_SHORT)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        mIndices.put(mIndicesData).position(0);
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        String vShaderStr = ESShader.readShader(mContext, "bindbuffer_vertexShader.glsl");
        String fShaderStr = ESShader.readShader(mContext, "bindbuffer_fragmentShader.glsl");

        // Load the shaders and get a linked program object
        mProgramObject = ESShader.loadProgram(vShaderStr, fShaderStr);

        glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        vPosition = glGetAttribLocation(mProgramObject, "vPosition");
        vColor = glGetAttribLocation(mProgramObject, "vColor");

        glGenBuffers(3, mVBOIds, 0);
        Log.e(TAG, "0: " + mVBOIds[0] + ", 1: " + mVBOIds[1] + ", 2: " + mVBOIds[2]);

        // mVBOIds[0] - used to store vertex position
        mVertices.position(0);
        glBindBuffer(GL_ARRAY_BUFFER, mVBOIds[0]);
        glBufferData(GL_ARRAY_BUFFER, POSITION_COMPONENT_SIZE * BYTES_PER_FLOAT * VERTICES_COUNT,
                mVertices, GL_STATIC_DRAW);

        // mVBOIds[1] - used to store vertex color
        mColors.position(0);
        glBindBuffer(GL_ARRAY_BUFFER, mVBOIds[1]);
        glBufferData(GL_ARRAY_BUFFER, COLOR_COMPONENT_SIZE * BYTES_PER_FLOAT * 4,
                mColors, GL_STATIC_DRAW);

        // mVBOIds[2] - used to store element indices
        mIndices.position(0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mVBOIds[2]);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, BYTES_PER_SHORT * 15, mIndices, GL_STATIC_DRAW);
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
        drawShape();
    }

    private void drawShape() {
        glBindBuffer(GL_ARRAY_BUFFER, mVBOIds[0]);
        glEnableVertexAttribArray(vPosition);
        glVertexAttribPointer(vPosition, POSITION_COMPONENT_SIZE,
                GL_FLOAT, false, POSITION_COMPONENT_SIZE * BYTES_PER_FLOAT, 0);

        glBindBuffer(GL_ARRAY_BUFFER, mVBOIds[1]);
        glEnableVertexAttribArray(vColor);
        glVertexAttribPointer(vColor, COLOR_COMPONENT_SIZE,
                GL_FLOAT, false, COLOR_COMPONENT_SIZE * BYTES_PER_FLOAT, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mVBOIds[2]);

        glDrawElements(GL_TRIANGLES, 15, GL_UNSIGNED_SHORT, 0);

        glDisableVertexAttribArray(vPosition);
        glDisableVertexAttribArray(vColor);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }
}
