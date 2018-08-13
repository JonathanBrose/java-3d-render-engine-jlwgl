#version 400 core

const int MAX_JOINTS = 50;
const int MAX_WEIGHTS = 3;
const int maxLights=4;

in vec3 position;
in vec2 textureCoordinates;
in vec3 normal;
in ivec3 in_jointIndices;
in vec3 in_weights;

out vec2 pass_textureCoordinates;
out vec3 surfaceNormal;
out vec3 toLightVector[maxLights];
out vec3 toCameraVector;
out float visibility;
out vec4 shadowCoords;
out float isUnderWater;


uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[maxLights];
uniform float numberOfRows;
uniform vec2 offset;
uniform mat4 toShadowSpace;
uniform float useFakeLighting;
uniform vec4 plane;
uniform mat4 jointTransforms[MAX_JOINTS];
uniform mat4 projectionViewMatrix;
uniform bool underwater;
uniform float underwaterheight;
uniform float underwaterDensity;
uniform float underwaterGradient;

const float density = 0.004;
const float gradient = 1.5;
const float shadowDistance = 150.0;
const float transitionDistance = 10.0;



void main(void){
	vec4 totalNormal = vec4(normal,0.0);
	vec4 totalLocalPos = vec4(0.0);
	
	
	for(int i=0;i<MAX_WEIGHTS;i++){
		mat4 jointTransform = jointTransforms[in_jointIndices[i]];
		vec4 posePosition = jointTransform * vec4(position,1.0);
		totalLocalPos += posePosition * in_weights[i];
		
		vec4 worldNormal = jointTransform * vec4(normal, 0.0);
		totalNormal += worldNormal * in_weights[i];
	}
	vec4 worldPosition = transformationMatrix * totalLocalPos;
	if(worldPosition.y < underwaterheight){
		isUnderWater = 1;
		}else {
		isUnderWater = 0;
		}
	shadowCoords = toShadowSpace * worldPosition;
	gl_ClipDistance[0] = dot(worldPosition, plane);
	
	vec4 positionRelativeToCamera = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCamera;
	
	pass_textureCoordinates = (textureCoordinates/numberOfRows)+ offset;
	
	vec3 actualNormal = totalNormal.xyz;
	if(useFakeLighting > 0.5){
		actualNormal = vec3(0.0,1.0,0.0);
	}
	
	surfaceNormal = (transformationMatrix * vec4(actualNormal,0.0)).xyz;
	for(int i =0; i<maxLights;i++){
	toLightVector[i] = lightPosition[i] - worldPosition.xyz;
	}
	toCameraVector = (inverse(viewMatrix) * vec4(0.0,0.0,0.0,1.0)).xyz - worldPosition.xyz;
		float distance = length(positionRelativeToCamera.xyz);
		if(underwater && isUnderWater == 1){
			visibility = exp(-pow((distance*underwaterDensity),underwaterGradient));
			visibility = clamp(visibility,0.0,1.0);
		}else{
			visibility = exp(-pow((distance*density),gradient));
			visibility = clamp(visibility,0.0,1.0);
		}
	
		distance = distance - (shadowDistance - transitionDistance);
		distance = distance / transitionDistance;
		shadowCoords.w = clamp(1.0 -distance,0.0,1.0);
}