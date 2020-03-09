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
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
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

public class GlDrawArraysRender implements GLSurfaceView.Renderer {
    private final float[] mVerticesTriangles =
            {
                    0.0f, 0.0f, 0.0f, // v0
                    0.0f, 0.5f, 0.0f, // v1
                    -0.5f, 0.0f, 0.0f, // v2

                    0.0f, 0.0f, 0.0f, // v0
                    -0.5f, 0.0f, 0.0f, // v2
                    -0.5f, -0.5f, 0.0f,  // v3

                    0.0f, 0.0f, 0.0f, // v0
                    -0.5f, -0.5f, 0.0f,  // v3
                    0.5f, -0.5f, 0.0f,  // v4

                    0.0f, 0.0f, 0.0f, // v0
                    0.5f, -0.5f, 0.0f,  // v4
                    0.5f, 0.0f, 0.0f,  // v5

                    0.0f, 0.0f, 0.0f, // v0
                    0.5f, 0.0f, 0.0f,  // v5
                    0.0f, 0.5f, 0.0f // v1
            };

    private final float[] mVerticesTrianglesStrip =
            {
                    0.0f, 0.0f, 0.0f, // v0
                    0.0f, 0.5f, 0.0f, // v1
                    -0.5f, 0.0f, 0.0f, // v2
                    -0.5f, -0.5f, 0.0f,  // v3
                    0.5f, -0.5f, 0.0f,  // v4
                    0.5f, 0.0f, 0.0f,  // v5
                    0.0f, 0.5f, 0.0f, // v1
            };

    private final float[] mVerticesTrianglesFan =
            {
                    0.0f, 0.0f, 0.0f, // v0
                    0.0f, 0.5f, 0.0f, // v1
                    -0.5f, 0.0f, 0.0f, // v2
                    -0.5f, -0.5f, 0.0f,  // v3
                    0.5f, -0.5f, 0.0f,  // v4
                    0.5f, 0.0f, 0.0f,  // v5
//                    0.0f, 0.5f, 0.0f, // v1
            };

    private static final String TAG = GlDrawArraysRender.class.getSimpleName();
    private static final int BYTES_PER_FLOAT = 4;
    private static final int POSITION_COMPONENT_SIZE = 3;
    private Context mContext;
    private int mProgramObject;
    private int vPosition;
    private FloatBuffer mVertices, mVerticesStrip, mVerticesFan;
    private int mWidth, mHeight;

    public GlDrawArraysRender(Context context) {
        mContext = context;
        mVertices = ByteBuffer.allocateDirect(mVerticesTriangles.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(mVerticesTriangles).position(0);

        mVerticesStrip = ByteBuffer.allocateDirect(mVerticesTrianglesStrip.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVerticesStrip.put(mVerticesTrianglesStrip).position(0);

        mVerticesFan = ByteBuffer.allocateDirect(mVerticesTrianglesFan.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVerticesFan.put(mVerticesTrianglesFan).position(0);
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
//        drawShape(GL_TRIANGLES);
//        drawShape(GL_TRIANGLE_STRIP);
        drawShape(GL_TRIANGLE_FAN);
    }

    private void loadProgram() {
        String vShaderStr = ESShader.readShader(mContext, "drawarrays_vertexShader.glsl");
        String fShaderStr = ESShader.readShader(mContext, "drawarrays_fragmentShader.glsl");
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

        Log.i(TAG, "vPosition: " + vPosition);
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

    private void drawShape(int type) {
        // Clear the color buffer
        glClear(GL_COLOR_BUFFER_BIT);
        glViewport(0, 0, mWidth, mHeight);

        // Use the program object
        glUseProgram(mProgramObject);

        if (GL_TRIANGLES == type) {
            mVertices.position(0);
            // Load the vertex data
            glVertexAttribPointer(vPosition, POSITION_COMPONENT_SIZE, GL_FLOAT, false, 0, mVertices);
            glEnableVertexAttribArray(vPosition);
            glDrawArrays(type, 0, 15);
        } else if (GL_TRIANGLE_STRIP == type) {
            mVertices.position(0);
            // Load the vertex data
            glVertexAttribPointer(vPosition, POSITION_COMPONENT_SIZE, GL_FLOAT, false, 0, mVerticesStrip);
            glEnableVertexAttribArray(vPosition);
            glDrawArrays(type, 0, 7);
        } else if (GL_TRIANGLE_FAN == type) {
            mVertices.position(0);
            // Load the vertex data
            glVertexAttribPointer(vPosition, POSITION_COMPONENT_SIZE, GL_FLOAT, false, 0, mVerticesFan);
            glEnableVertexAttribArray(vPosition);
            glDrawArrays(type, 0, 6);
        }

        glEnableVertexAttribArray(vPosition);
    }
}
