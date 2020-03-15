precision mediump float;

uniform sampler2D uTextureUnit;
varying vec2 vTexturePosition;
varying vec4 fColor;

void main()
{
    gl_FragColor = texture2D(uTextureUnit, vTexturePosition);
//    gl_FragColor *= fColor;
}