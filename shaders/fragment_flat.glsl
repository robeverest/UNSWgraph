
out vec4 outputColor;

uniform vec4 input_color;

flat in vec3 intensity;

void main()
{
    outputColor = vec4(intensity,1) * input_color;
}
