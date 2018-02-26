#version 330

in vec2 outTexCoord;
in vec3 mvPos;

out vec4 fragColor;

uniform sampler2D textureSampler;
uniform vec4 color;
uniform int hasTexture;

void main() {
    if (hasTexture == 1) {
        fragColor = color * texture(textureSampler, outTexCoord);
    } else {
        fragColor = color;
    }
}
