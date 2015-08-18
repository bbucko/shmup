#version 410

in vec3 position;
in vec3 color;
in vec2 texcoord;

out vec3 vertexColor;
out vec2 textureCoord;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
    vertexColor = color;

    mat4 mvp = projection * view * model;
    gl_Position = mvp * vec4(position, 1.0);
    textureCoord = texcoord;
}