attribute vec4 aBlendPosition;
attribute vec2 aBlendTexturePosition;

varying vec2 vBlendTexturePosition;

void main()
{
    vBlendTexturePosition = aBlendTexturePosition;
    gl_Position = aBlendPosition;
}