#version 450 core
in vec3 vertexColor;
out vec4 FragColor;

//uniform vec3 uniformColor;

void main() {
    FragColor = vec4(vertexColor, 1.0);
    //FragColor = vec4(vertexColor * uniformColor, 1.0f);
}