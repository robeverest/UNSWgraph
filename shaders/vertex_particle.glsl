
// Incoming vertex position
in vec3 velocity;

// Incoming color
in vec4 color;

uniform mat4 model_matrix;

uniform mat4 view_matrix;

uniform mat4 proj_matrix;

uniform int time;

uniform float gravity;

out vec4 fragColor;

void main() {
	vec3 position = vec3(0,0,0);

	vec4 lifeColor = color;

	lifeColor.a = 1 - 0.002*time;

	position += velocity*time + 0.5*vec3(0,gravity,0)*time*time;

	// The global position is in homogenous coordinates
    vec4 globalPosition = model_matrix * vec4(position, 1);

    // The position in camera coordinates
    vec4 viewPosition = view_matrix * globalPosition;

    // The position in CVV coordinates
    gl_Position = proj_matrix * viewPosition;

    fragColor = lifeColor;
}
