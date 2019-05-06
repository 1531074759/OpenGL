package com.lime.common;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Cube {

    private final float[] cubeVerts = {
            -0.5f, 0.5f, 0.5f,     // (0) Top-left near
            0.5f, 0.5f, 0.5f,     // (1) Top-right near
            -0.5f, -0.5f, 0.5f,     // (2) Bottom-left near
            0.5f, -0.5f, 0.5f,     // (3) Bottom-right near
            -0.5f, 0.5f, -0.5f,     // (4) Top-left far
            0.5f, 0.5f, -0.5f,     // (5) Top-right far
            -0.5f, -0.5f, -0.5f,     // (6) Bottom-left far
            0.5f, -0.5f, -0.5f,      // (7) Bottom-right far
    };

    private final short[] cubeIndices = new short[]{
            // Front
            0, 2, 1,
            1, 2, 3,

            // Back
            4, 7, 5,
            5, 7, 6,

            // Left
            0, 2, 4,
            4, 2, 6,

            // Right
            5, 7, 1,
            1, 7, 3,

            // Top
            5, 1, 4,
            4, 1, 0,

            // Bottom
            6, 2, 7,
            7, 2, 3
    };

    public Cube() {

    }

    public int genCube(float scale) {
        // Allocate memory for buffers
        mVertices = ByteBuffer.allocateDirect(cubeVerts.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mIndices = ByteBuffer.allocateDirect(cubeIndices.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer();

        mVertices.put(cubeVerts).position(0);

        for (int i = 0; i < cubeVerts.length; i++) {
            mVertices.put(i, mVertices.get(i) * scale);
        }
        mIndices.put(cubeIndices).position(0);
        mNumIndices = cubeIndices.length;
        return mNumIndices;
    }

    public FloatBuffer getVertices() {
        return mVertices;
    }

    public ShortBuffer getIndices() {
        return mIndices;
    }

    public int getNumIndices() {
        return mNumIndices;
    }

    // Member variables
    private FloatBuffer mVertices;
    private ShortBuffer mIndices;
    private int mNumIndices;
}
