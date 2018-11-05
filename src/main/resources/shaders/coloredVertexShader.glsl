#version 330
layout(location=0) in vec2 position;
layout(location=1) in vec3 color;
out vec4 vColor;
uniform mat4 model;
uniform mat4 projection;
void main(void)
{
   gl_Position = projection * model * vec4(position, 0.0, 1.0);
   vColor = vec4(color, 1.0);
}