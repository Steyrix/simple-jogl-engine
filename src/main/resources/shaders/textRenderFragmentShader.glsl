#version 330
in vec2 fragmentUV;
out vec4 fColor;
uniform sampler2D textureSample;
void main(void)
{
    fColor = texture(textureSample, fragmentUV).rgba;

    if(fColor.a <= 0 || (fColor.r == 0 && fColor.g == 0 && fColor.b == 0)){
        discard;
    }
}

