package com.opengl.learn;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.lime.common.ESShader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetError;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

public class GlViewportRender implements GLSurfaceView.Renderer {
    private static String TAG = GlViewportRender.class.getSimpleName();
    private final float[] mVerticesData =
            {0.0f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
                    -0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f,
                    0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f};

    private final float[] mColorData =
            {
                    1.0f, 0.0f, 0.0f, 1.0f,   // c0
                    0.0f, 1.0f, 0.0f, 1.0f,   // c1
                    0.0f, 0.0f, 1.0f, 1.0f    // c2
            };

    private static final int BYTES_PER_FLOAT = 4;
    private static final int POSITION_COMPONENT_SIZE = 3;
    private static final int COLOR_COMPONENT_SIZE = 4;

    private Context mContext;
    private int mProgramObject;
    private int mWidth;
    private int mHeight;
    private FloatBuffer mVertices, mColorVertices;
    private int vPosition, vColor;

    private static final int STRIDE =
            (POSITION_COMPONENT_SIZE + COLOR_COMPONENT_SIZE) * BYTES_PER_FLOAT;

    public GlViewportRender(Context context) {
        mContext = context;
        mVertices = ByteBuffer.allocateDirect(mVerticesData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(mVerticesData).position(0);
        mColorVertices = ByteBuffer.allocateDirect(mColorData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mColorVertices.put(mColorData).position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        loadProgram();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        drawShape();
    }

    private void drawShape() {
        // Clear the color buffer
        glClear(GL_COLOR_BUFFER_BIT);
        for (int i = 0; i < 4; i++) {
            if (i == 0) {
                glViewport(0, mHeight / 2, mWidth / 2, mHeight / 2);
            } else if (i == 1) {
                glViewport(mWidth / 2, mHeight / 2, mWidth / 2, mHeight / 2);
            } else if (i == 2) {
                glViewport(0, 0, mWidth / 2, mHeight / 2);
            } else if (i == 3) {
                glViewport(mWidth / 2, 0, mWidth / 2, mHeight / 2);
            }

            // Use the program object
            glUseProgram(mProgramObject);

            mVertices.position(0);
            // Load the vertex data
            glVertexAttribPointer(vPosition, 3, GL_FLOAT, false, STRIDE, mVertices);
            glEnableVertexAttribArray(vPosition);

            mVertices.position(POSITION_COMPONENT_SIZE);
            glVertexAttribPointer(vColor, 4, GL_FLOAT, false, STRIDE, mVertices);
            glEnableVertexAttribArray(vColor);

            glDrawArrays(GL_TRIANGLES, 0, 3);
        }
    }

    private void loadProgram() {
        String vShaderStr = ESShader.readShader(mContext, "viewport_vertexShader.glsl");
        String fShaderStr = ESShader.readShader(mContext, "viewport_fragmentShader.glsl");
        int vertexShader;
        int fragmentShader;
        int programObject;
        int[] linked = new int[1];

        // Load the vertex/fragment shaders
        vertexShader = loadShader(GL_VERTEX_SHADER, vShaderStr);
        fragmentShader = loadShader(GL_FRAGMENT_SHADER, fShaderStr);

        // Create the program object
        programObject = glCreateProgram();

        if (programObject == 0) {
            return;
        }

        glAttachShader(programObject, vertexShader);
        glAttachShader(programObject, fragmentShader);

        // Link the program
        glLinkProgram(programObject);

        // Check the link status
        glGetProgramiv(programObject, GL_LINK_STATUS, linked, 0);

        if (linked[0] == 0) {
            Log.e(TAG, "Error linking program:");
            Log.e(TAG, glGetProgramInfoLog(programObject));
            glDeleteProgram(programObject);
            return;
        }
        // Store the program object
        mProgramObject = programObject;
        glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        vPosition = glGetAttribLocation(mProgramObject, "vPosition");
        vColor = glGetAttribLocation(mProgramObject, "vColor");

        Log.i(TAG, "vPosition: " + vPosition + ", vColor: " + vColor);
    }

    private int loadShader(int type, String shaderSrc) {
        int shader;
        int[] compiled = new int[1];

        // Create the shader object
        shader = glCreateShader(type);

        if (shader == 0) {
            return 0;
        }

        // Load the shader source
        glShaderSource(shader, shaderSrc);

        // Compile the shader
        glCompileShader(shader);

        // Check the compile status
        glGetShaderiv(shader, GL_COMPILE_STATUS, compiled, 0);

        if (compiled[0] == 0) {
            Log.e(TAG, "compile shader error: " + glGetShaderInfoLog(shader));
            glGetError();
            glDeleteShader(shader);
            return 0;
        }
        Log.i(TAG, "load " + type + " shader result: " + shader);
        return shader;
    }
}
