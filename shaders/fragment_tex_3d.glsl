
out vec4 outputColor;

uniform vec4 input_color;

uniform sampler2D tex;

in vec2 texCoordFrag;

void main()
{
    // Output whatever was input
    outputColor = input_color*texture(tex, texCoordFrag);
}
