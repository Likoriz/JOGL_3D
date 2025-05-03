#version 450 core

struct Material {
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    float shininess;
};

struct Light {
    int type;

    vec3 position;
    vec3 direction;
    float cutOff;

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
    //view mode
    if (wireframeMode)
        outColor = vec4(vertColor, 1.0f);
    else {
        if (light.type == 1) { //Directional light
            vec3 ambient = light.ambient * material.ambient;
            vec3 norm = normalize(vertNormal);
            vec3 lightDir = -light.position;

            float diffK = max(dot(norm, -lightDir), 0.0f);
            vec3 diffuse = light.diffuse * (diffK * material.diffuse);

           vec3 reflectDir = reflect(-lightDir, norm);
           vec3 viewDir = normalize(fragPos - viewPos);

           float specK = pow(max(dot(viewDir, reflectDir), 0.0f), material.shininess);
           //vec3 specular = light.specular * (specK * material.specular);
           vec3 specular = vec3(0.1f, 0.1f, 0.1f);

           outColor = texture(ourTexture, texCoords) * vec4(ambient + diffuse + specular, 1.0f);
        }
        else if (light.type == 2) { //Point light
            float dist = distance(light.position, fragPos);
            float attenuation = 1.0f / (light.constant + light.linear * dist + light.quadratic * dist * dist);

            vec3 ambient = light.ambient * material.ambient * attenuation;

            //diffuse
            vec3 norm = normalize(vertNormal);
            vec3 lightDir = normalize(fragPos - light.position);

            float diffK = max(dot(norm, -lightDir), 0.0f);
            vec3 diffuse = light.diffuse * (diffK * material.diffuse) * attenuation;

            //specular
            vec3 reflectDir = reflect(-lightDir, norm);
            vec3 viewDir = normalize(fragPos - viewPos);

            float specK = pow(max(dot(viewDir, reflectDir), 0.0f), material.shininess);
            vec3 specular = light.specular * (specK * material.specular) * attenuation;

           outColor = texture(ourTexture, texCoords) * vec4(ambient + diffuse + specular, 1.0f);
        }
        else if (light.type == 3) { // Spot light
            vec3 lightDir = -normalize(fragPos - light.position);
            float angle = acos(dot(lightDir, normalize(-light.direction)));

            if (angle <= light.cutOff * 2.0f) {
                float k = 1.0f;

                if (angle >= light.cutOff)
                    k = (light.cutOff * 2.0f - angle) / light.cutOff;

                float dist = distance(light.position, fragPos);
                float attenuation = 1.0f / (light.constant + light.linear * dist + light.quadratic * dist * dist);

                vec3 ambient = light.ambient * material.ambient * attenuation;

                //diffuse
                vec3 norm = normalize(vertNormal);
                vec3 lightDir = normalize(fragPos - light.position);

                float diffK = max(dot(norm, -lightDir), 0.0f);
                vec3 diffuse = light.diffuse * (diffK * material.diffuse) * attenuation;
                diffuse *= k;

                //specular
                vec3 reflectDir = reflect(-lightDir, norm);
                vec3 viewDir = normalize(fragPos - viewPos);

                float specK = pow(max(dot(viewDir, reflectDir), 0.0f), material.shininess);
                vec3 specular = light.specular * (specK * material.specular) * attenuation;
                specular *= k;

                outColor = texture(ourTexture, texCoords) * vec4(ambient + diffuse + specular, 1.0f);
            }
            else
                outColor = texture(ourTexture, texCoords) * vec4(material.ambient * light.ambient, 1.0f);
        }
        else
            outColor = texture(ourTexture, texCoords);
    }
}