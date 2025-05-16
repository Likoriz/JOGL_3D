#version 330 core
out vec4 FragColor;

in vec2 TexCoords;

uniform sampler2D scene;
uniform vec2 waveOrigin;
uniform float waveRadius;
uniform bool spreadingColor;
uniform float edgeSoftness;

void main()
{
    vec3 color = texture(scene, TexCoords).rgb;
    vec3 grayscale = vec3(dot(color, vec3(0.299, 0.587, 0.114)));

    float dist = distance(TexCoords, waveOrigin);

    float transition = smoothstep(waveRadius - edgeSoftness, waveRadius + edgeSoftness, dist);

    vec3 result = spreadingColor ? mix(color, grayscale, transition) : mix(grayscale, color, transition);
    FragColor = vec4(result, 1.0);
}
