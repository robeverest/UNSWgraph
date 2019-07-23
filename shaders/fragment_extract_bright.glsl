
out vec4 outputColor;

uniform vec4 input_color;

uniform sampler2D tex;

in vec2 texCoordFrag;

void main()
{
	vec2 texelSize = 1.0/textureSize(tex, 0);

	vec4 original = texture(tex, gl_FragCoord.xy*texelSize);

	// Brightness is perceptual, give different weights to each primary.
	float brightness = dot(original.rgb, vec3(0.2126, 0.7152, 0.0722));

	// If the alpha component is low then so is the brightness
	brightness *= original.a;

	if (brightness > 0.6)
		outputColor = original;
	else
		outputColor = vec4(0,0,0,0);
}
