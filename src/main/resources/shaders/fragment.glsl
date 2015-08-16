#version 150 core

in vec3 vertexColor;
in vec2 textureCoord;

out vec4 outColor;

uniform sampler2D firstTexture;
uniform sampler2D secondTexture;
uniform float time;

void main() {
    vec4 firstTextureColor = texture(firstTexture, textureCoord);
    vec4 secondTextureColor = texture(secondTexture, textureCoord);


    if(textureCoord.y > 0.5) {
        firstTextureColor = texture(firstTexture,  vec2(textureCoord.x, 1 - textureCoord.y));
        secondTextureColor = texture(secondTexture, vec2(textureCoord.x, 1 - textureCoord.y));
    }


    outColor = mix(firstTextureColor, secondTextureColor, time);

//    outColor = mix(firstTextureColor, secondTextureColor, time) * vec4(vertexColor, 1.0);
//    outColor = vec4(vertexColor, 1.0) * textureColor;
//    outColor = vec4(vertexColor, 1.0);
}