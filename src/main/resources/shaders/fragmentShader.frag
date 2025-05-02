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
    float constant;
    float linear;
    float quadratic;
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
    float dist = distance(light.position, fragPos);
    float attenuation = 1.0f / (light.constant + light.linear * dist + light.quadratic * dist * dist);

    vec3 ambient = light.ambient * material.ambient * attenuation;

    //diffuse
    vec3 norm = normalize(vertNormal);
    vec3 lightDir = normalize(fragPos - light.position);

    float diffK = max(dot(norm, -lightDir), 0.0f);
    vec3 diffuse = light.diffuse * (diffK * material.diffuse) * attenuation;

    //specular
//    vec3 lightPos = light.position;
//    float specK = 0;
//    for (float i = -0.2; i <= 0.2; i += 0.05) {
//        lightPos.y = light.position.y + i;
//        vec3 slightDir = normalize(fragPos - lightPos);
//        vec3 reflectDir = reflect(-slightDir, norm);
//        vec3 viewDir = normalize(fragPos - viewPos);
//        specK += pow(max(dot(viewDir, reflectDir), 0.0f), material.shininess * 20.f);
//    }

    vec3 reflectDir = reflect(-lightDir, norm);
    vec3 viewDir = normalize(fragPos - viewPos);

    float specK = pow(max(dot(viewDir, reflectDir), 0.0f), material.shininess);
    vec3 specular = light.specular * (specK * material.specular) * attenuation;

    //view mode
    if (wireframeMode)
        outColor = vec4(vertColor, 1.0f);
    else
        outColor = texture(ourTexture, texCoords) * vec4(ambient + diffuse + specular, 1.0f);
}