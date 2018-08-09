#version 400
in vec2 fragmentUV;
out vec4 fColor;
uniform sampler2D textureAtlas;
void main(void)
{
    fColor = texture(textureAtlas, fragmentUV).rgba;

    if(fColor.a <= 0){
        discard;
    }

}
