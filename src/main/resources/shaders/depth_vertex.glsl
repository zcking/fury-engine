#version 330

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;
layout (location = 2) in vec3 vertexNormal;

uniform mat4 modelLightViewMatrix;  // analogous to view model matrix for a camera
uniform mat4 orthoProjectionMatrix; // for point lights this would be a perspective PM

void main() {
    gl_Position = orthoProjectionMatrix * modelLightViewMatrix * vec4(position, 1.0f);
}
