// The MIT License (MIT)
//
// Copyright (c) 2013 Dan Ginsburg, Budirijanto Purnomo
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

//
// Book:      OpenGL(R) ES 3.0 Programming Guide, 2nd Edition
// Authors:   Dan Ginsburg, Budirijanto Purnomo, Dave Shreiner, Aaftab Munshi
// ISBN-10:   0-321-93388-5
// ISBN-13:   978-0-321-93388-1
// Publisher: Addison-Wesley Professional
// URLs:      http://www.opengles-book.com
//            http://my.safaribooksonline.com/book/animation-and-3d/9780133440133
//

// VertexArrayObjects
//
//    This is a simple example drawing a primitive with
//    Vertex Array Objects (VAOs)
//

package com.lime.opengl.render.es3;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import com.lime.common.ESShader;

public class VAORenderer implements GLSurfaceView.Renderer {

    private Context mContext;

    ///
    // Constructor
    //
    public VAORenderer(Context context) {
        mContext = context;
        mVertices = ByteBuffer.allocateDirect(mVerticesData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(mVerticesData).position(0);

        mIndices = ByteBuffer.allocateDirect(mIndicesData.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        mIndices.put(mIndicesData).position(0);
    }

    ///
    // Initialize the shader and program object
    //
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        String vShaderStr = ESShader.readShader(mContext, "chapter66/vertexShader.vert");
        String fShaderStr = ESShader.readShader(mContext, "chapter66/fragmentShader.frag");

        // Load the shaders and get a linked program object
        mProgramObject = ESShader.loadProgram(vShaderStr, fShaderStr);

        // Generate VBO Ids and load the VBOs with data
        GLES30.glGenBuffers(2, mVBOIds, 0);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVBOIds[0]);

        mVertices.position(0);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, mVerticesData.length * 4,
                mVertices, GLES30.GL_STATIC_DRAW);

        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, mVBOIds[1]);

        mIndices.position(0);
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, 2 * mIndicesData.length,
                mIndices, GLES30.GL_STATIC_DRAW);

        // Generate VAO Id
        GLES30.glGenVertexArrays(1, mVAOId, 0);

        // Bind the VAO and then setup the vertex
        // attributes
        GLES30.glBindVertexArray(mVAOId[0]);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVBOIds[0]);
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, mVBOIds[1]);

        GLES30.glEnableVertexAttribArray(VERTEX_POS_INDX);
        GLES30.glEnableVertexAttribArray(VERTEX_COLOR_INDX);

        GLES30.glVertexAttribPointer(VERTEX_POS_INDX, VERTEX_POS_SIZE,
                GLES30.GL_FLOAT, false, VERTEX_STRIDE,
                0);

        GLES30.glVertexAttribPointer(VERTEX_COLOR_INDX, VERTEX_COLOR_SIZE,
                GLES30.GL_FLOAT, false, VERTEX_STRIDE,
                (VERTEX_POS_SIZE * 4));

        // Reset to the default VAO
        GLES30.glBindVertexArray(0);

        GLES30.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
    }

    // /
    // Draw a triangle using the shader pair created in onSurfaceCreated()
    //
    public void onDrawFrame(GL10 glUnused) {
        // Set the viewport
        GLES30.glViewport(0, 0, mWidth, mHeight);

        // Clear the color buffer
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        // Use the program object
        GLES30.glUseProgram(mProgramObject);

        // Bind the VAO
        GLES30.glBindVertexArray(mVAOId[0]);

        // Draw with the VAO settings
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, mIndicesData.length, GLES30.GL_UNSIGNED_SHORT, 0);

        // Return to the default VAO
        GLES30.glBindVertexArray(0);
    }

    ///
    // Handle surface changes
    //
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    // Handle to a program object
    private int mProgramObject;

    // Additional member variables
    private int mWidth;
    private int mHeight;
    private FloatBuffer mVertices;
    private ShortBuffer mIndices;

    // VertexBufferObject Ids
    private int[] mVBOIds = new int[2];

    // VertexArrayObject Id
    private int[] mVAOId = new int[1];

    // 3 vertices, with (x,y,z) ,(r, g, b, a) per-vertex
    private final float[] mVerticesData =
            {
                    0.0f, 0.5f, 0.0f,        // v0
                    1.0f, 0.0f, 0.0f, 1.0f,  // c0
                    -0.5f, -0.5f, 0.0f,        // v1
                    0.0f, 1.0f, 0.0f, 1.0f,  // c1
                    0.5f, -0.5f, 0.0f,        // v2
                    0.0f, 0.0f, 1.0f, 1.0f,  // c2
            };

    private final short[] mIndicesData =
            {
                    0, 1, 2
            };

    final int VERTEX_POS_SIZE = 3; // x, y and z
    final int VERTEX_COLOR_SIZE = 4; // r, g, b, and a

    final int VERTEX_POS_INDX = 0;
    final int VERTEX_COLOR_INDX = 1;

    final int VERTEX_STRIDE = (4 * (VERTEX_POS_SIZE + VERTEX_COLOR_SIZE));
}
