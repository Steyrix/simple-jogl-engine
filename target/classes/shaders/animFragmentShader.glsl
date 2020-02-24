#version 400
in vec2 fragmentUV;
out vec4 fColor;
uniform sampler2D textureSample;
void main(void)
{
    fColor = texture(textureSample, fragmentUV).rgba;
    if(fColor.a <= 0){
        discard;
        }
}
