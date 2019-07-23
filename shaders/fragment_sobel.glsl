
out vec4 outputColor;

uniform vec4 input_color;

uniform sampler2D tex;

in vec2 texCoordFrag;

void main()
{
	vec2 texelSize = 1.0/textureSize(tex, 0);

	float threshold = 0.7;

	// Matrices are in column major order
	mat3 g_x = mat3(-1, -2, -1, 0, 0, 0, 1, 2, 1);
	mat3 g_y = mat3(-1, 0, 1, -2, 0, 2, -1, 0, 1);

	vec3 s_x = vec3(0,0,0);
	vec3 s_y = vec3(0,0,0);
	for (int y = 0; y < 3; y++) {
		for (int x = 0; x < 3; x++) {
			s_x += g_x[x][y]*texture(tex, texCoordFrag + vec2(x-1,y-1)*texelSize).rgb;
			s_y += g_y[x][y]*texture(tex, texCoordFrag + vec2(x-1,y-1)*texelSize).rgb;
		}
	}

	vec3 mag = sqrt(s_x*s_x + s_y*s_y);
	if (mag.r < threshold && mag.y < threshold && mag.z < threshold) {
		outputColor = vec4(0, 0, 0, 0); //Completely transparent
	} else {
		outputColor = vec4(0, 0, 0, 1);
	}
//	outputColor = vec4(mag,1);
}
