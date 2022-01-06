#version 330
in vec2 fragmentUV;
out vec4 fColor;
uniform sampler2D textureSample;
void main(void)
{
    fColor = texture(textureSample, fragmentUV).rgba * vec4(1.0, 1.0, 1.0, 1.0);

    if(fColor.a <= 0){
        discard;
    }
}