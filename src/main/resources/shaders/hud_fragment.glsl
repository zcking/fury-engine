#version 330

in vec2 outTexCoord;
in vec3 mvPos;

out vec4 fragColor;

uniform sampler2D textureSampler;
uniform vec4 color;

void main() {
    fragColor = color * texture(textureSampler, outTexCoord);
}
