precision mediump float;

uniform sampler2D uBlendTextureUnit;
varying vec2 vBlendTexturePosition;

void main()
{
    gl_FragColor = texture2D(uBlendTextureUnit, vBlendTexturePosition);
}