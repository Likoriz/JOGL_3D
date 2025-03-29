#version 450 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 inColor;

out vec3 vertexColor;
//uniform vec3 uniformPosition;
uniform mat4 pvm;

void main() {
//    vec3 newPos = aPos / 2 + uniformPosition / 2;
//    vec3 dir = (newPos - vec3(0, 0, 0)) / distance(vec3(0, 0, 0), newPos);

//    gl_Position = vec4(newPos + dir / 3.0f, 1.0f);
    gl_Position = pvm * vec4(aPos, 1.0f);
    vertexColor = inColor;
}