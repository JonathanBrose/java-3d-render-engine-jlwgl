#version 400 core

in vec3 position;


out vec3 Color;



uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform vec3 OutlineColor;



void main(void){
	
	
	vec4 worldPosition = transformationMatrix * vec4(position,1.0);
	vec4 positionRelativeToCamera = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCamera;

	Color = OutlineColor;
	
	
				
	
}