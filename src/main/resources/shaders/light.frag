#version 330 core

out vec4 outColor;
out vec4 BrightColor;

uniform vec3 lightColor;

void main()
{
    outColor = vec4(lightColor, 1.0f);

    float brightness = dot(lightColor, vec3(0.2126, 0.7152, 0.0722));

    if (brightness > 1.0)
        BrightColor = vec4(lightColor, 1.0f);
    else
        BrightColor = vec4(0.0);
}