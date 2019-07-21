
#define FLT_MAX 3.402823466e+38

#define NUM_SPHERES 2

#define NUM_LIGHTS 1

#define SPEED 0.02

#define AMBIENT 0.4
#define PHONG 32
#define SPHERE_SHININESS 0.5

out vec4 outputColor;

uniform int windowWidth;
uniform int windowHeight;

uniform int time;

vec3 background = vec3(1,1,1);

struct Ray {
	vec3 pos;
	vec3 dir;
};

struct Hit {
	float t;
	vec3 pos;
	vec3 normal;
	vec3 colour;
	float shininess;
};

struct Sphere {
	vec3 pos;
	vec3 colour;
};

// Lights are achromatic
struct Light {
	vec3 pos;
	float diffuse;
	float specular;
};

Sphere spheres[NUM_SPHERES];

Light lights[NUM_LIGHTS];

// Create a ray from the camera through the given pixel
Ray rayThroughPixel(vec2 pixel) {
	float w = 1;
	float h = 1;
	float c = windowWidth;
	float r = windowHeight;
	float n = 1.73f; //This gives a FOV of approx 60 degrees

	float i_c = w * (2*pixel.x/c - 1);
	float j_r = h * (2*pixel.y/r - 1);

	Ray ray;
	ray.pos = vec3(0,0,0);
	ray.dir = vec3(i_c, j_r, -n);

	return ray;
}

// Offset a ray along its direction
Ray offsetRay(Ray ray, float dt) {
	return Ray(ray.pos + dt*ray.dir, ray.dir);
}

// Get the colour of on the plane at the given point
vec3 getPlaneColour(vec3 pos) {
	//Using logical XOR (^^) here
	if (mod(round(pos.x), 2) == 0 ^^ mod(round(pos.z), 2) == 0) {
		return vec3(0,0,0);
	} else {
		return vec3(1,1,1);
	}
}

// Find the hit point of a ray through an x-z plane at y=-1
Hit planeHit(Ray ray) {
	//Transform ray into plane's coordinate system
	ray.pos.y++;

	//See if the ray collides with the plane
	float t_hit = - ray.pos.y / ray.dir.y;

	Hit hit;
	hit.t = t_hit;

	if (t_hit > 0 && t_hit != FLT_MAX) {
		//Get the colour of the object at that point on the plane
		vec3 p_hit = ray.pos + t_hit*ray.dir + vec3(0,-1,0);
		hit.pos = p_hit;
		hit.normal = vec3(0,1,0);
		hit.colour = getPlaneColour(p_hit);
		hit.shininess = 0;
	}
	return hit;
}

// Find the hit point of a ray with a sphere in the spheres array.
Hit sphereHit(int sphere, Ray ray) {
	//Transform into spheres coordinate system
	ray.pos -= spheres[sphere].pos;

	//Solve via quadratic formula
	float a = dot(ray.dir, ray.dir); // |v|^2 = v . v
	float b = 2*dot(ray.pos, ray.dir);
	float c = dot(ray.pos, ray.pos) - 1;

	float d = b*b - 4*a*c;

	float t_hit;

	if (d >= 0) {
		//We have at least one solution
		float t1 = (-b + sqrt(d)) / (2*a);
		float t2 = (-b - sqrt(d)) / (2*a);
		if (t1 > 0 && t2 > 0)
			t_hit = min(t1, t2);
		else if (t1 < 0 && t2 < 0)
			t_hit = FLT_MAX;
		else
			t_hit = max(t1, t2);
	} else {
		t_hit = FLT_MAX;
	}

	Hit hit;
	hit.t = t_hit;
	hit.colour = spheres[sphere].colour;
	hit.pos = ray.pos + t_hit*ray.dir + spheres[sphere].pos;
	hit.normal = normalize(hit.pos - spheres[sphere].pos);
	hit.shininess = SPHERE_SHININESS;

	return hit;
}

// Trace a ray through the scene
Hit traceRay(Ray ray) {
	bool hitPlane = false;
	int hitSphere = -1;

    Hit firstHit;
    firstHit.t = FLT_MAX;

    int i;
    for (i = 0; i < NUM_SPHERES; i++) {
    	Hit hit = sphereHit(i, ray);
    	if (hit.t > 0 && hit.t < firstHit.t) {
    		firstHit = hit;
    	}
    }

    // Slight hack. We always hit spheres before the plane.
    if (firstHit.t < 0 || firstHit.t == FLT_MAX) {
    	firstHit = planeHit(ray);
    }

    if (firstHit.t < 0 || firstHit.t == FLT_MAX) {
    	firstHit.colour = background;
    }
    return firstHit;
}

// Apply lighting from the light with index i to the given hit point
vec3 applyLight(Hit hit, int i) {
	Light light = lights[i];

	vec3 s = normalize(light.pos - hit.pos);
	vec3 r = reflect(-s,hit.normal);
	vec3 v = -hit.pos;

	//The diffuse magnitude
	float diff_mag = light.diffuse * dot(s, hit.normal);

	//The specular magnitude
	float spec_mag = dot(normalize(r), normalize(v));

	//If surface is facing us
	if (diff_mag > 0)
		spec_mag = light.specular * hit.shininess * pow(spec_mag, PHONG);
	else
		spec_mag = diff_mag = 0;

	//The final colour. We assume that everything has white specular highlights.
	return vec3(diff_mag)*hit.colour + vec3(spec_mag);
}

// Trace a ray but also trace shadow feelers and calculate the lighting influence.
Hit traceRayWithLight(Ray ray) {
	//Get the sample from just firing a single ray
	Hit unlit = traceRay(ray);

	Hit lit = unlit;

	if (lit.t > 0 && lit.t != FLT_MAX) {
		//Start by just adding ambient light
		lit.colour *= AMBIENT;

		//Send out shadow feelers to all the lights
		int i;
		for (i = 0; i < NUM_LIGHTS; i++) {
			Ray feeler = Ray(unlit.pos, lights[i].pos - unlit.pos);
			// Find occluding objects, ignore objects hit at starting point of ray to avoid
			// self collision issues.
			Hit occlusion = traceRay(offsetRay(feeler, 0.0001f));
			if (occlusion.t < 0 || occlusion.t == FLT_MAX) {
				//Nothing in between this point and the light so add this lights contribution
				//to the hit.
				lit.colour += applyLight(unlit, i);
			}
		}
	}
	return lit;
}


void main()
{
	spheres[0].pos = vec3(cos(time*SPEED), 0, -4 + sin(time*SPEED));
	spheres[0].colour = vec3(0.4f,0.4f,0);

	spheres[1].pos = vec3(-cos(time*SPEED), 0, -4 - sin(time*SPEED));
	spheres[1].colour = vec3(0.4f,0,0.4f);

	lights[0] = Light(vec3(1, 1, -2), 0.8, 1);

	Ray ray = rayThroughPixel(gl_FragCoord.xy);

	// Offset so ray starts at the screen
	ray = offsetRay(ray, 1);

	Hit hit = traceRayWithLight(ray);

    //Trace reflected rays.
	Hit hit2 = hit;
	float shininess = 1;
	int count = 0;

	while (hit2.t > 0 && hit2.t != FLT_MAX && count < 2 && shininess >= 0.001) {
		shininess *= hit2.shininess;
		//If we're no longer very shiny we're not going to be reflecting much
		if (shininess < 0.001)
			break;
		ray = Ray(hit2.pos, reflect(ray.dir, hit2.normal));
		hit2 = traceRayWithLight(offsetRay(ray, 0.001f));

		if (hit2.t > 0 && hit2.t != FLT_MAX)
			hit.colour += hit2.colour*shininess;
		count++;

	}

	outputColor = vec4(hit.colour,1);
}
