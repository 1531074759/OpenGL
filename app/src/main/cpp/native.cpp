#include <jni.h>
#include <string>
#include <android/hardware_buffer.h>
#include <android/hardware_buffer_jni.h>

#define EGL_EGLEXT_PROTOTYPES
#include <EGL/egl.h>
#include <EGL/eglext.h>
#include <EGL/eglplatform.h>

#define GL_GLEXT_PROTOTYPES
#include <GLES3/gl3.h>
#include <GLES2/gl2ext.h>

extern "C"
JNIEXPORT void JNICALL
Java_com_lime_opengl_utils_NativeMethods_processPreview(JNIEnv *env, jclass type, jobject bufObj) {
    AHardwareBuffer *buffer = AHardwareBuffer_fromHardwareBuffer(env, bufObj);
    AHardwareBuffer_Desc desc;
    AHardwareBuffer_describe(buffer, &desc);

    EGLClientBuffer clientBuffer = eglGetNativeClientBufferANDROID(buffer);
    eglCreateImageKHR(NULL, NULL, EGL_NATIVE_BUFFER_ANDROID, clientBuffer, 0);

    glEGLImageTargetTexture2DOES(GL_TEXTURE_EXTERNAL_OES, NULL);
    eglDestroyImageKHR(NULL, NULL);
}