#version 400
layout(location=0) in vec2 position;
layout(location=1) in vec2 vertexUV;
out vec2 fragmentUV;
uniform mat4 model;
uniform mat4 projection;
uniform int frameNumberX;
uniform int frameNumberY;
uniform float xOffset;
uniform float yOffset;
void main(void)
{
   gl_Position = projection * model * vec4(position, 0.0, 1.0);

   fragmentUV = vec2(vertexUV.x * frameNumberX, vertexUV.y * frameNumberY);

   if(fragmentUV.x <= 0)
   {
   fragmentUV.x = vertexUV.x + xOffset;
   }

   if(fragmentUV.y <= 0)
   {
   fragmentUV.y = vertexUV.y + yOffset;
   }
}
