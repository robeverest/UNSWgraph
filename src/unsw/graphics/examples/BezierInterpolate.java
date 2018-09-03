package unsw.graphics.examples;


import java.awt.Color;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL3;

import unsw.graphics.Application2D;
import unsw.graphics.Matrix3;
import unsw.graphics.Shader;
import unsw.graphics.geometry.LineStrip2D;
import unsw.graphics.geometry.Point2D;


/** 
 *  The up,down,left,arrows move the current control point
 *  The space bar changes to the next control point
 * @author Angela
 * @author Rob
 *
 */


public class BezierInterpolate extends Application2D implements KeyListener {
    private int currentPoint ; 
    private Point2D[] controlPoints;
    private int segments;
    
	public BezierInterpolate() {
        super("Quadratic bezier interpolation", 600, 600);
        currentPoint = 0;
        controlPoints = new Point2D[] {
            new Point2D(0, 0.5f),
            new Point2D(-1,1.5f),
            new Point2D(0, 2.5f),
            new Point2D(-1,4f)
        };
        segments = 32;
    }
	
    public static void main(String[] args) {
        BezierInterpolate example = new BezierInterpolate();
        example.start();

    }   
    
    private float getX(float t){
    	return (1 - t)*(1 - t)*(1 - t) * controlPoints[0].getX() + 
    	        3*t * (1-t) * (1 - t) * controlPoints[1].getX() + 
    	        3*t * t * (1-t) * controlPoints[2].getX() + 
    	        t*t*t * controlPoints[3].getX();
    }
    
    private float getY(float t){
    	return (1 - t)*(1 - t)*(1 - t) * controlPoints[0].getY() + 
    	        3*t * (1-t) * (1 - t) * controlPoints[1].getY() + 
    	        3*t * t * (1-t) * controlPoints[2].getY() + 
    	        t*t*t * controlPoints[3].getY();
    }
    
    @Override
    public void init(GL3 gl) {
        super.init(gl);
        getWindow().addKeyListener(this);
    }
  
    @Override
    public void display(GL3 gl) {
    	super.display(gl);
    	
    	Shader.setViewMatrix(gl, Matrix3.scale(0.2f, 0.2f));
        
    	Shader.setPenColor(gl, Color.RED);
    	controlPoints[0].draw(gl);
    	
    	Shader.setPenColor(gl, Color.GREEN);
        controlPoints[1].draw(gl);
        
        Shader.setPenColor(gl, Color.BLUE);
        controlPoints[2].draw(gl);
        
        Shader.setPenColor(gl, Color.BLACK);
        controlPoints[3].draw(gl);
        
        LineStrip2D curve = new LineStrip2D();
    	float dt = 1.0f/segments;
    	
    	for(int i = 0; i <= segments; i++){        		
    		float t = i*dt;
    		curve.add(new Point2D(getX(t), getY(t)));
    	}
    	
    	curve.draw(gl);
    }
   
	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_SPACE:			
			currentPoint++;
			currentPoint = currentPoint%controlPoints.length;
			System.out.println(currentPoint);
			break;
		case KeyEvent.VK_LEFT:
			controlPoints[currentPoint] =
			   controlPoints[currentPoint].translate(-0.1f, 0);
			break;

		case KeyEvent.VK_RIGHT:
            controlPoints[currentPoint] =
            controlPoints[currentPoint].translate(0.1f, 0);
			break;

		case KeyEvent.VK_DOWN:
            controlPoints[currentPoint] =
            controlPoints[currentPoint].translate(0, -0.1f);
			break;

		case KeyEvent.VK_UP:
	         controlPoints[currentPoint] =
             controlPoints[currentPoint].translate(0, 0.1f);
			break;

		default:
			break;
		}
	}



	@Override
	public void keyReleased(KeyEvent arg0) {
		
	}

}
