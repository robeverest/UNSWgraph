
// Incoming vertex position
in vec3 position;

// Incoming color
in vec4 color;

uniform mat4 model_matrix;

uniform mat4 view_matrix;

uniform mat4 proj_matrix;

uniform int time;

uniform float gravity;

out vec4 fragColor;

void main() {
	// Velocity is passed in as the position attribute
	vec3 velocity = position;

	vec3 pos = time * velocity + vec3(0, 0.5*gravity*time*time, 0);

	// The global position is in homogenous coordinates
    vec4 globalPosition = model_matrix * vec4(pos, 1);

    // The position in camera coordinates
    vec4 viewPosition = view_matrix * globalPosition;

    // The position in CVV coordinates
    gl_Position = proj_matrix * viewPosition;

    fragColor = color;
    fragColor.a = 1 - time*0.002;
}
