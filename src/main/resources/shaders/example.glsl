VERTEX:
#version 330
layout(location=0) in vec2 position;

#ifdef USE_TEXTURE
    layout(location=1) in vec2 vertexUV;
    out vec2 fragmentUV;
#else
    layout(location=1) in vec3 color;
    out vec4 vColor;
#endif

uniform mat4 model;
uniform mat4 projection;
void main(void)
{
   gl_Position = projection * model * vec4(position, 0.0, 1.0);
   #ifdef USE_TEXTURE
    fragmentUV = vertexUV;
   #else
    vColor = vec4(color, 1.0);
   #endif
}

FRAGMENT:
#version 330

#ifdef USE_TEXTURE
    in vec2 fragmentUV;
    uniform sampler2D textureSample;
#else
    in vec4 vColor;
#endif

out vec4 fColor;

void main(void)
{
    #ifdef USE_TEXTURE
        fColor = texture(textureSample, fragmentUV).rgba;
    #else
        fColor = vColor;
    #endif
}



