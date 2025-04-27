#version 450 core
layout (location = 0) in vec3 inPos;
layout (location = 1) in vec3 inNormal;
layout (location = 2) in vec2 inTexCoords;
layout (location = 3) in vec3 inColors;

out vec3 vertColor;
out vec2 texCoords;

uniform mat4 pvm;

void main() {
    gl_Position = pvm * vec4(inPos, 1.0f);
    vertColor = inColors;
    texCoords = inTexCoords;
}