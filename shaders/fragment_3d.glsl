
out vec4 outputColor;

uniform vec3 input_color;

void main()
{
    // Output whatever was input
    outputColor = vec4(input_color, 0);
}
