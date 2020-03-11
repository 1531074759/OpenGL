attribute vec4 vPosition;
attribute vec4 vColor;

varying vec4 fColor;

void main()
{
    fColor = vColor;
    gl_Position = vPosition;
}