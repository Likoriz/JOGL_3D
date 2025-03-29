#version 450 core
in vec3 vertexColor;
in vec2 texCoords;

out vec4 FragColor;

uniform sampler2D ourTexture;

void main() {
    //FragColor = vec4(vertexColor, 1.0);
    FragColor = texture(ourTexture, texCoords) * vec4(vertexColor, 1.0);
}