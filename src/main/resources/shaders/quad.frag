#version 330 core
out vec4 FragColor;

in vec2 TexCoords;

uniform sampler2D hdrColorBuffer;

void main()
{
    vec3 hdrColor = texture(hdrColorBuffer, TexCoords).rgb;
    FragColor = vec4(hdrColor, 1.0);
}