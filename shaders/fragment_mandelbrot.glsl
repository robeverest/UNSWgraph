
out vec4 outputColor;

uniform vec4 input_color;

in vec2 localPosition;

void main()
{
	vec2 c = localPosition;
	vec2 z = vec2(0);

	int i = 0;

	for (i = 0; i < 128; i++) {
		z = vec2(z.x*z.x - z.y*z.y, 2*z.x*z.y) + c;

		if (z.x*z.x + z.y*z.y > 4) break;
	}

//	if (i == 128)
//		outputColor = vec4(0,0,0,1);
//	else
//		outputColor = vec4(1,1,1,1);

	//One possible colouring. Play around with it.
	float i2 = (128-i)/128.0;
	outputColor = vec4(i2, fract(i2*2), fract(i2*3), 1);
}
