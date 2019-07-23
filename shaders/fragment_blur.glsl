
out vec4 outputColor;

uniform vec4 input_color;

uniform sampler2D tex;

uniform bool horizontal;

in vec2 texCoordFrag;

void main()
{
	vec2 texelSize = 1.0/textureSize(tex, 0);

	// Kernel is symmetric, so we only store half of it.
	float weights[5] = float[] (0.227027, 0.1945946, 0.1216216, 0.054054, 0.016216);

	vec3 result = texture(tex, texCoordFrag).rgb*weights[0];

	if (horizontal) {
		for (int i = 1; i < 5; i++) {
			result += weights[i]*texture(tex, texCoordFrag + vec2(i,0)*texelSize).rgb;
			result += weights[i]*texture(tex, texCoordFrag - vec2(i,0)*texelSize).rgb;
		}
	} else {
		for (int i = 1; i < 5; i++) {
			result += weights[i]*texture(tex, texCoordFrag + vec2(0,i)*texelSize).rgb;
			result += weights[i]*texture(tex, texCoordFrag - vec2(0,i)*texelSize).rgb;
		}
	}

	outputColor = vec4(result, 1);
}
