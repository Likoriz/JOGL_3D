#version 450 core

in vec3 vertColor;
in vec2 texCoords;
in vec3 vertNormal;
in vec3 fragPos;

out vec4 outColor;

uniform sampler2D ourTexture;
uniform bool wireframeMode;

uniform vec3 viewPos;
uniform vec3 lightPos;
uniform vec3 lightColor;
uniform vec3 ambientColor;

void main() {
    vec3 ambient = ambientColor * 0.2f;

    vec3 norm = normalize(vertNormal);
    vec3 lightDir = normalize(fragPos - lightPos);

    float diffK = max(dot(norm, -lightDir), 0.0f);
    vec3 diffuse = diffK * lightColor;

    vec3 viewDir = normalize(fragPos - viewPos);
    vec3 reflectDir = reflect(-lightDir, norm);

    float specularStrength = 2.0f;
    float specK = pow(max(dot(viewDir, reflectDir), 0.0f), 128);
    vec3 specular = specularStrength * specK * lightColor;

    if (wireframeMode)
        outColor = vec4(vertColor, 1.0f);
    else
        outColor = texture(ourTexture, texCoords) * vec4(ambient + diffuse + specular, 1.0f);
}