
// Incoming vertex position
in vec2 position;

uniform mat3 model_matrix;

void main() {
	// The global position is in homogenous coordinates
    vec3 globalPosition = model_matrix * vec3(position, 1);

    // We must convert from a homogenous coordinate in 2D to a homogenous
    // coordinate in 3D.
    gl_Position = vec4(globalPosition.xy, 0, 1);
}
