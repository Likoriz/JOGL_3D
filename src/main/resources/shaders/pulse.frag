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
    if (waveRadius < 0.0) {
        FragColor = vec4(color, 1.0); // волна еще не начиналась
        return;
    }

    vec3 grayscale = vec3(dot(color, vec3(0.299, 0.587, 0.114)));

    float dist = distance(TexCoords, waveOrigin);

    float transition = smoothstep(waveRadius - edgeSoftness, waveRadius + edgeSoftness, dist);

    //vec3 result = spreadingColor ? mix(color, grayscale, transition) : mix(grayscale, color, transition);
    vec3 result = spreadingColor ? mix(grayscale, color, transition) : mix(color, grayscale, transition);
    FragColor = vec4(result, 1.0);
}
