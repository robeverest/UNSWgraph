
out vec4 outputColor;

uniform vec4 input_color;

uniform sampler2D tex;

in vec4 fragColor;

void main()
{
    // Output whatever was input
    outputColor = input_color*fragColor*texture(tex, gl_PointCoord);
}
