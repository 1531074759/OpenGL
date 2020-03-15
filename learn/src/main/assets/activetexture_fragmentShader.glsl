precision mediump float;

uniform sampler2D uTextureUnit;
varying vec2 vTexturePosition;
varying vec4 fColor;

void main()
{
    gl_FragColor = texture2D(uTextureUnit, vTexturePosition);
//    gl_FragColor *= fColor;
}





//uniform sampler2D u_TextureUnit;
//varying vec2 v_TextureCoordinates;
//
//void main()
//{
//    gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);
//}