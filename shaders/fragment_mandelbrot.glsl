
out vec4 outputColor;

uniform vec4 input_color;

in vec3 globalPosition;

void main()
{
    // Output whatever was input
	vec4 color = input_color;

	float R = globalPosition.x;
	float Im = globalPosition.y;

	int i = 0;

	for (i = 0; i < 128; i++) {
		float R2  = R*R - Im*Im + globalPosition.x;
		float Im2 = 2*R*Im + globalPosition.y;

		if (R2*R2 + Im2*Im2 > 4) break;
		R = R2;
		Im = Im2;
	}

//	if (i == 128)
//		outputColor = vec4(0,0,0,1);
//	else
//		outputColor = vec4(1,1,1,1);

	//One possible colouring. Play around with it.
	float i2 = (128-i)/127.0;
	outputColor = vec4(i2, fract(i2*2), fract(i2*3), 1);
}
