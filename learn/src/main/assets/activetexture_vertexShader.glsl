attribute vec4 aPosition;
attribute vec4 aColor;
attribute vec2 aTexturePosition;

varying vec4 fColor;
varying vec2 vTexturePosition;

void main()
{
    fColor = aColor;
    vTexturePosition = aTexturePosition;
    gl_Position = aPosition;
}