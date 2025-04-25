#version 450 core
in vec3 vertexColor;
in vec2 texCoords;

out vec4 FragColor;

uniform sampler2D ourTexture;
uniform bool wireframeMode;

void main() {
    if (wireframeMode)
        FragColor = vec4(vertexColor, 1.0f);
    else
        FragColor = texture(ourTexture, texCoords) * vec4(vertexColor, 1.0f);
}