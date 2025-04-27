#version 450 core

layout (location = 0) in vec3 inPos;
layout (location = 1) in vec3 inNormal;
layout (location = 2) in vec2 inTexCoords;
layout (location = 3) in vec3 inColors;

out vec3 vertColor;
out vec2 texCoords;
out vec3 vertNormal;
out vec3 fragPos;

uniform mat4 pv;
uniform mat4 model;

void main() {
    vec4 vertPos;
    vertPos = model * vec4(inPos, 1.0f);
    gl_Position = pv * model * vec4(inPos, 1.0f);
    vertColor = inColors;
    texCoords = inTexCoords;
    vertNormal = mat3(model) * inNormal;
    fragPos = vertPos.xyz;
}