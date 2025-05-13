#version 430 core

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
in vec3 FragPos;
in mat3 TBN;

layout (location = 0) out vec4 FragColor;
layout (location = 1) out vec4 BrightColor;

uniform sampler2D texture_diffuse1;
uniform sampler2D texture_specular1;
uniform sampler2D texture_normal1;
uniform sampler2D texture_emissive1;
uniform float shininess = 64.0f;

uniform vec3 viewPos;
#define MAX_LIGHTS 4
uniform int lights_count;
uniform Light light[MAX_LIGHTS];

float getAtten(int i)
{
    float dist = distance(light[i].position, FragPos);
    return 1.0 / (light[i].constant + light[i].linear * dist + light[i].quadratic * dist * dist);
}

vec3 CalcDiffusePlusSpecular(int i, vec3 lightDir)
{
    vec3 norm = texture(texture_normal1, texCoords).rgb;
    norm = normalize(norm * 2.0f - 1.0f);
    norm = normalize(TBN * norm);

    float diff_koef = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = light[i].diffuse * diff_koef * vec3(texture(texture_diffuse1, texCoords));

    vec3 reflectDir = reflect(lightDir, norm);
    vec3 viewDir = normalize(FragPos - viewPos);
    float spec_koef = pow(max(dot(viewDir, reflectDir), 0.0f), shininess);
    vec3 specular = light[i].specular * spec_koef * vec3(texture(texture_specular1, texCoords));

    return diffuse + specular;
}

void main()
{
    vec3 lresult = vec3(0.0);

    for (int i = 0; i < lights_count; i++)
    {
        vec3 lightDir = normalize(light[i].position - FragPos);

        if (light[i].type == 1) // Directional
        {
            lightDir = -light[i].direction;
            vec3 ambient = light[i].ambient * texture(texture_diffuse1, texCoords).rgb;
            vec3 diffspec = CalcDiffusePlusSpecular(i, lightDir);
            lresult += ambient + diffspec;
        }
        else if (light[i].type == 2) // Point
        {
            float attenuation = getAtten(i);
            vec3 ambient = light[i].ambient * texture(texture_diffuse1, texCoords).rgb;
            vec3 diffspec = CalcDiffusePlusSpecular(i, lightDir);
            lresult += (ambient + diffspec) * attenuation;
        }
        else if (light[i].type == 3) // Spot
        {
            float angle = acos(dot(lightDir, normalize(-light[i].direction)));
            if (angle <= light[i].cutOff * 2.0f)
            {
                float koef = 1.0f;
                if (angle >= light[i].cutOff)
                koef = (light[i].cutOff * 2.0f - angle) / light[i].cutOff;

                float attenuation = getAtten(i);
                vec3 ambient = light[i].ambient * texture(texture_diffuse1, texCoords).rgb;
                vec3 diffspec = CalcDiffusePlusSpecular(i, lightDir) * koef;
                lresult += (ambient + diffspec) * attenuation;
            }
            else
            {
                lresult += texture(texture_diffuse1, texCoords).rgb * light[i].ambient;
            }
        }
    }

    // Glow
    vec3 emissiveColor = texture(texture_emissive1, texCoords).rgb;
    vec3 norm = normalize(TBN * (texture(texture_normal1, texCoords).rgb * 2.0 - 1.0));
    vec3 viewDir = normalize(viewPos - FragPos);
    float facing = 1.0 - max(dot(norm, viewDir), 0.0);
    float intensity = length(emissiveColor);
    vec3 glow = emissiveColor * facing * intensity * 40.0;

    vec3 result = lresult + emissiveColor + glow;

    FragColor = vec4(result, 1.0);

    float brightness = dot(result, vec3(0.2126, 0.7152, 0.0722));
    if (brightness > 1.0)
    BrightColor = vec4(result, 1.0);
    else
    BrightColor = vec4(0.0, 0.0, 0.0, 1.0);
}
