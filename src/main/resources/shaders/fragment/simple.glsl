#version 410 core

in vec3 vertexColor;
in vec2 textureCoord;

out vec4 outColor;

uniform sampler2D firstTexture;

void main() {
    outColor = texture(firstTexture, textureCoord);
}
