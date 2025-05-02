#version 450 core

struct Material {
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    float shininess;
};

struct Light {
    vec3 position;
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

in vec3 vertColor;
in vec2 texCoords;
in vec3 vertNormal;
in vec3 fragPos;

out vec4 outColor;

uniform sampler2D ourTexture;
uniform bool wireframeMode;

uniform vec3 viewPos;
uniform Material material;
uniform Light light;

void main() {
    vec3 ambient = light.ambient * material.ambient;

    //diffuse
    vec3 norm = normalize(vertNormal);
    vec3 lightDir = normalize(fragPos - light.position);

    float diffK = max(dot(norm, -lightDir), 0.0f);
    vec3 diffuse = light.diffuse * (diffK * material.diffuse);

    //specular
    vec3 reflectDir = reflect(-lightDir, norm);
    vec3 viewDir = normalize(fragPos - viewPos);

    float specK = pow(max(dot(viewDir, reflectDir), 0.0f), material.shininess);
    vec3 specular = light.specular * (specK * material.specular);

    if (wireframeMode)
        outColor = vec4(vertColor, 1.0f);
    else
        outColor = texture(ourTexture, texCoords) * vec4(ambient + diffuse + specular, 1.0f);
}