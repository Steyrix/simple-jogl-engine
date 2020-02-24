#version 330
layout(location=0) in vec2 position;
out vec4 vColor;
uniform mat4 model;
uniform mat4 projection;
void main(void)
{
    gl_Position = projection * model * vec4(position, 0.0, 1.0);
    vColor = vec4(1.0, 0.0, 0.0, 1.0);
}