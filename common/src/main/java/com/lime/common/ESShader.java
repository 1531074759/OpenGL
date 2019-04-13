package com.lime.common;

import android.content.Context;
import android.opengl.GLES30;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ESShader {

    private static final String TAG = ESShader.class.getSimpleName();

    //
    ///
    /// \brief Read a shader source into a String
    /// \param context Application context
    /// \param fileName Name of shader file
    /// \return A String object containing shader source, otherwise null
    //
    public static String readShader(Context context, String fileName) {
        String shaderSource = null;

        if (fileName == null) {
            return shaderSource;
        }

        // Read the shader file from assets
        InputStream is = null;
        byte[] buffer;

        try {
            is = context.getAssets().open(fileName);
            // Create a buffer that has the same size as the InputStream
            buffer = new byte[is.available()];
            // Read the text file as a stream, into the buffer
            is.read(buffer);

            ByteArrayOutputStream os = new ByteArrayOutputStream();

            // Write this buffer to the output stream
            os.write(buffer);

            // Close input and output streams
            os.close();
            is.close();

            shaderSource = os.toString();
        } catch (IOException ioe) {
            Log.i(TAG, "read shader error.", ioe);
            is = null;
        }

        if (is == null) {
            return shaderSource;
        }
        return shaderSource;
    }

    //
    ///
    /// \brief Load a shader, check for compile errors, print error messages to
    /// output log
    /// \param type Type of shader (GL_VERTEX_SHADER or GL_FRAGMENT_SHADER)
    /// \param shaderSrc Shader source string
    /// \return A new shader object on success, 0 on failure
    //
    public static int loadShader(int type, String shaderSrc) {
        int shader;
        int[] compiled = new int[1];

        // Create the shader object
        shader = GLES30.glCreateShader(type);

        if (shader == 0) {
            return 0;
        }

        // Load the shader source
        GLES30.glShaderSource(shader, shaderSrc);

        // Compile the shader
        GLES30.glCompileShader(shader);

        // Check the compile status
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0);

        if (compiled[0] == 0) {
            Log.e("ESShader", GLES30.glGetShaderInfoLog(shader));
            GLES30.glDeleteShader(shader);
            return 0;
        }

        return shader;
    }

    //
    ///
    /// \brief Load a vertex and fragment shader, create a program object, link
    ///    program.
    /// Errors output to log.
    /// \param vertShaderSrc Vertex shader source code
    /// \param fragShaderSrc Fragment shader source code
    /// \return A new program object linked with the vertex/fragment shader
    ///    pair, 0 on failure
    //
    public static int loadProgram(String vertShaderSrc, String fragShaderSrc) {
        int vertexShader;
        int fragmentShader;
        int programObject;
        int[] linked = new int[1];

        // Load the vertex/fragment shaders
        vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertShaderSrc);

        if (vertexShader == 0) {
            return 0;
        }

        fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragShaderSrc);

        if (fragmentShader == 0) {
            GLES30.glDeleteShader(vertexShader);
            return 0;
        }

        // Create the program object
        programObject = GLES30.glCreateProgram();

        if (programObject == 0) {
            return 0;
        }

        GLES30.glAttachShader(programObject, vertexShader);
        GLES30.glAttachShader(programObject, fragmentShader);

        // Link the program
        GLES30.glLinkProgram(programObject);

        // Check the link status
        GLES30.glGetProgramiv(programObject, GLES30.GL_LINK_STATUS, linked, 0);

        if (linked[0] == 0) {
            Log.e("ESShader", "Error linking program:");
            Log.e("ESShader", GLES30.glGetProgramInfoLog(programObject));
            GLES30.glDeleteProgram(programObject);
            return 0;
        }

        // Free up no longer needed shader resources
        GLES30.glDeleteShader(vertexShader);
        GLES30.glDeleteShader(fragmentShader);

        return programObject;
    }

    //
    ///
    /// \brief Load a vertex and fragment shader from "assets", create a program object, link
    ///    program.
    /// Errors output to log.
    /// \param vertShaderFileName Vertex shader source file name
    /// \param fragShaderFileName Fragment shader source file name
    /// \return A new program object linked with the vertex/fragment shader
    ///    pair, 0 on failure
    //
    public static int loadProgramFromAsset(Context context, String vertexShaderFileName, String fragShaderFileName) {
        int vertexShader;
        int fragmentShader;
        int programObject;
        int[] linked = new int[1];

        String vertShaderSrc = null;
        String fragShaderSrc = null;

        // Read vertex shader from assets
        vertShaderSrc = readShader(context, vertexShaderFileName);

        if (vertShaderSrc == null) {
            return 0;
        }

        // Read fragment shader from assets
        fragShaderSrc = readShader(context, fragShaderFileName);

        if (fragShaderSrc == null) {
            return 0;
        }

        // Load the vertex shader
        vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertShaderSrc);

        if (vertexShader == 0) {
            return 0;
        }

        // Load the fragment shader
        fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragShaderSrc);

        if (fragmentShader == 0) {
            GLES30.glDeleteShader(vertexShader);
            return 0;
        }

        // Create the program object
        programObject = GLES30.glCreateProgram();

        if (programObject == 0) {
            return 0;
        }

        GLES30.glAttachShader(programObject, vertexShader);
        GLES30.glAttachShader(programObject, fragmentShader);

        // Link the program
        GLES30.glLinkProgram(programObject);

        // Check the link status
        GLES30.glGetProgramiv(programObject, GLES30.GL_LINK_STATUS, linked, 0);

        if (linked[0] == 0) {
            Log.e("ESShader", "Error linking program:");
            Log.e("ESShader", GLES30.glGetProgramInfoLog(programObject));
            GLES30.glDeleteProgram(programObject);
            return 0;
        }

        // Free up no longer needed shader resources
        GLES30.glDeleteShader(vertexShader);
        GLES30.glDeleteShader(fragmentShader);

        return programObject;
    }
}
