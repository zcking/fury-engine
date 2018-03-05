#version 330

const int MAX_POINT_LIGHTS = 5;
const int MAX_SPOT_LIGHTS = 5;

in vec2 outTexCoord;
in vec3 mvVertexNormal;
in vec3 mvVertexPos;
in mat4 outModelViewMatrix;
in vec4 mLightViewVertexPos;

out vec4 fragColor;

struct Attenuation {
    float constant;
    float linear;
    float exponent;
};

struct PointLight {
    vec3 color;
    // Light position assumed to be in View coordinates
    vec3 position;
    float intensity; // range of 0 - 1
    Attenuation att;
};

struct SpotLight {
    PointLight pointLight;
    vec3 coneDir;
    float cutOff;
};

struct DirectionalLight {
    vec3 color;
    vec3 direction;
    float intensity;
};

struct Material {
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    int hasTexture;
    float reflectance;
    int hasNormalMap;
};

struct Fog {
    int enabled;
    vec3 color;
    float density;
};

uniform sampler2D textureSampler;
uniform sampler2D normalMap;
uniform sampler2D shadowMap;
uniform vec3 ambientLight;
uniform float specularPower;
uniform Material material;
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform DirectionalLight directionalLight;
uniform SpotLight spotLights[MAX_SPOT_LIGHTS];
uniform Fog fog;

vec4 ambientC;
vec4 diffuseC;
vec4 specularC;

void setupColors(Material material, vec2 textCoord) {
    if (material.hasTexture == 1) {
        ambientC = texture(textureSampler, textCoord);
        diffuseC = ambientC;
        specularC = ambientC;
    } else {
        ambientC = material.ambient;
        diffuseC = material.diffuse;
        specularC = material.specular;
    }
}

vec4 calcLightColor(vec3 lightColor, float lightIntensity, vec3 position, vec3 toLightDir, vec3 normal) {
    vec4 diffuseColor = vec4(0, 0, 0, 0);
    vec4 specularColor = vec4(0, 0, 0, 0);

    // Diffuse light
    float diffuseFactor = max(dot(normal, toLightDir), 0.0);
    diffuseColor = diffuseC * vec4(lightColor, 1.0) * lightIntensity * diffuseFactor;

    // Specular Light
    vec3 cameraDirection = normalize(-position); // assumes camera always at origin; TODO: pass in camera pos and subtract?
    vec3 fromLightSource = -(toLightDir);
    vec3 reflectedLight = normalize(reflect(fromLightSource, normal));
    float specularFactor = max(dot(cameraDirection, reflectedLight), 0.0);
    specularFactor = pow(specularFactor, specularPower);
    specularColor = specularC * specularFactor * material.reflectance * vec4(lightColor, 1.0);

    return (diffuseColor + specularColor);
}

vec4 calcPointLight(PointLight light, vec3 position, vec3 normal) {
    vec3 lightDirection = light.position - position;
    vec3 toLightDir = normalize(lightDirection);
    vec4 lightColor = calcLightColor(light.color, light.intensity, light.position, toLightDir, normal);

    // Attenuation
    float distance = length(lightDirection);
    float attenuationInv = light.att.constant + light.att.linear * distance + light.att.exponent * distance * distance;

    return lightColor / attenuationInv;
}

vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal) {
    return calcLightColor(light.color, light.intensity, position, normalize(light.direction), normal);
}

vec4 calcSpotLight(SpotLight light, vec3 position, vec3 normal) {
    vec3 lightDirection = light.pointLight.position - position;
    vec3 toLightDir = normalize(lightDirection);
    vec3 fromLightDir = -(toLightDir);
    float spotAlpha = dot(fromLightDir, normalize(light.coneDir));

    vec4 color = vec4(0, 0, 0, 0);

    if (spotAlpha > light.cutOff) {
        color = calcPointLight(light.pointLight, position, normal);
        color *= (1.0 - (1.0 - spotAlpha) / (1.0 - light.cutOff));
    }

    return color;
}

vec4 calcFog(vec3 pos, vec4 color, Fog fog, vec3 ambientLight, DirectionalLight directionalLight) {
    vec3 fogColor = fog.color * (ambientLight + directionalLight.color * directionalLight.intensity);

    float distance = length(pos);
    float fogFactor = 1.0 / exp((distance * fog.density) * (distance * fog.density));
    fogFactor = clamp(fogFactor, 0.0, 1.0);

    vec3 resultColor = mix(fogColor, color.xyz, fogFactor);
    return vec4(resultColor.xyz, color.w);
}

vec3 calcNormal(Material material, vec3 normal, vec2 textCoord, mat4 modelViewMatrix) {
    vec3 newNormal = normal;
    if ( material.hasNormalMap == 1 )
    {
        newNormal = texture(normalMap, textCoord).rgb;
        newNormal = normalize(newNormal * 2 - 1);
        newNormal = normalize(modelViewMatrix * vec4(newNormal, 0.0)).xyz;
    }
    return newNormal;
}

float calcShadow(vec4 position) {
    // TODO: research how to fix the "peter panning" effect caused by the bias

    // TODO: research how to calculate shadows based on multiple light sources (e.g. new depth map per light source)
    vec3 projCoords = position.xyz;
    projCoords = projCoords * 0.5 + 0.5;
    float bias = 0.05;

    float shadowFactor = 0.0;
    vec2 inc = 1.0 / textureSize(shadowMap, 0);
    for (int row = -1; row <= 1; ++row) {
        for (int col = -1; col <= 1; ++col) {
            float texDepth = texture(shadowMap, projCoords.xy + vec2(row, col) * inc).r;
            shadowFactor += projCoords.z - bias > texDepth ? 1.0 : 0.0;
        }
    }
    shadowFactor /= 9.0;

    // Tranform screen coords to texture coords
    if (projCoords.z > 1.0) {
        // Current fragment is not in the shade
        shadowFactor = 1.0;
    }

    return 1 - shadowFactor;
}

void main() {
    setupColors(material, outTexCoord);

    vec3 curNormal = calcNormal(material, mvVertexNormal, outTexCoord, outModelViewMatrix);

    vec4 diffuseSpecularComp = calcDirectionalLight(directionalLight, mvVertexPos, curNormal);

    for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
        if (pointLights[i].intensity > 0) {
            diffuseSpecularComp += calcPointLight(pointLights[i], mvVertexPos, curNormal);
        }
    }

    for (int i = 0; i < MAX_SPOT_LIGHTS; i++) {
        if (spotLights[i].pointLight.intensity > 0) {
            diffuseSpecularComp += calcSpotLight(spotLights[i], mvVertexPos, curNormal);
        }
    }

    float shadow = calcShadow(mLightViewVertexPos);
    fragColor = clamp(ambientC * vec4(ambientLight, 1) + diffuseSpecularComp * shadow, 0, 1);

    if (fog.enabled == 1) {
        fragColor = calcFog(mvVertexPos, fragColor, fog, ambientLight, directionalLight);
    }
}
