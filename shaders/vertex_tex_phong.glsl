// A shader that performs Phong shading by interpolating the normals and
// coordinates of each vertex in camera coordinates.
//
// Note: This shader assumes there is no non-uniform scale in either the view
// or the model transform.

// Incoming vertex position
in vec3 position;

// Incoming normal
in vec3 normal;

// Incoming texture coordinate
in vec2 texCoord;

uniform mat4 model_matrix;

uniform mat4 view_matrix;

uniform mat4 proj_matrix;

out vec4 viewPosition;
out vec3 m;

out vec2 texCoordFrag;

void main() {
	// The global position is in homogenous coordinates
    vec4 globalPosition = model_matrix * vec4(position, 1);

    // The position in camera coordinates
    viewPosition = view_matrix * globalPosition;

    // The position in CVV coordinates
    gl_Position = proj_matrix * viewPosition;

    // Compute the normal in view coordinates
    m = normalize(view_matrix*model_matrix * vec4(normal, 0)).xyz;

    texCoordFrag = texCoord;
}
