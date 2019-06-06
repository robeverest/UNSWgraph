package unsw.graphics.scene.tests;

import org.junit.Test;

import junit.framework.TestCase;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.scene.Scene;
import unsw.graphics.scene.SceneObject;

/**
 * COMMENT: Comment SceneObjectTest 
 *
 * @author malcolmr
 * @author Robert Clifton-Everest
 * 
 */
public class SceneObjectTest extends TestCase {

    // to account for rounding errors on floats, we will
    // test to within epsilon of the correct answer:
 
    private static final float EPSILON = 0.001f;
    
    @Test
    public void testGlobal0() {
        Scene scene = new Scene();
        SceneObject obj = new SceneObject(scene.getRoot());
        
        Point2D p = obj.getGlobalPosition();
        float r = obj.getGlobalRotation();
        float s = obj.getGlobalScale();
        
        assertEquals(0, p.getX(), EPSILON);
        assertEquals(0, p.getY(), EPSILON);
        assertEquals(0, r, EPSILON);
        assertEquals(1, s, EPSILON);
    }

    @Test
    public void testGlobal1() {
        Scene scene = new Scene();
        SceneObject obj = new SceneObject(scene.getRoot());
        
        obj.translate(-2, 3);
        obj.rotate(90);
        obj.scale(2);
        
        Point2D p = obj.getGlobalPosition();
        float r = obj.getGlobalRotation();
        float s = obj.getGlobalScale();
        
        assertEquals(-2, p.getX(), EPSILON);
        assertEquals(3, p.getY(), EPSILON);
        assertEquals(90, r, EPSILON);
        assertEquals(2, s, EPSILON);
    }

    /**
     * Computing position is not simple a matter of adding up the origins. 
     * If the parent frame is rotated or scaled then the answer you get for 
     * the child will be wrong. 
     * 
     * You need to multiply the child's origin by the parent's model matrix 
     * to get the right answer.
     * 
     *  In this example the parent frame is rotated 90 and scaled by 2. 
     *  So the model matrix is:
     *  
     *  [ 0  -2  -2 ]
     *  [ 2   0   3 ]  
     *  [ 0   0   1 ]
     *  
     *  The i-axis is given by the 1st column: (0,2)
     *  The j-axis is given by the 2nd column: (-2,0)
     *  The origin is given by the 3rd column: (-2,3)
     *  
     *  The child is at (1,0) in the parent's frame, which is:
     *  
     *     phi + 1 * i + 0 * j = (-2,3) + 1 * (0,2) + 0 * (-2,0)
     *                         = (-2,5) in world coordinates
     *
     *        |(1,0)
     *      C +--
     *        |
     *    ----+ (-2, 3)
     *        P
     *            |
     *            +--
     *            W
     *            
     * W = world coordinate frame: origin = (0,0)   i = (1,0)     j = (0,1)
     * P = parent coordinate frame: origin = (-2,3) i = (0,2)     j = (-2, 0)  [in world coordinates]
     * C = child coordinate frame: origin = (1,0),  i = (0, -0.5) j = (0.5, 0) [in parent coordinates]
     *                             origin = (-2,5)  i = (1,0)     j = (0, 1)   [in world coordinates]
     */
    @Test
    public void testGlobal2() {
        Scene scene = new Scene();
        SceneObject parent = new SceneObject(scene.getRoot());
        SceneObject child = new SceneObject(parent);
        
        parent.translate(-2, 3);
        parent.rotate(90);
        parent.scale(2);
        
        // the child is also moved:
        
        Point2D p = child.getGlobalPosition();
        float r = child.getGlobalRotation();
        float s = child.getGlobalScale();
        
        assertEquals(-2, p.getX(), EPSILON);
        assertEquals(3, p.getY(), EPSILON);
        assertEquals(90, r, EPSILON);
        assertEquals(2, s, EPSILON);        
        
        // now move the child in its new coordinate frame
        
        child.translate(1, 0);
        child.rotate(-90);
        child.scale(0.5f);

        p = child.getGlobalPosition();
        r = child.getGlobalRotation();
        s = child.getGlobalScale();
        
        assertEquals(-2, p.getX(), EPSILON);
        assertEquals(5, p.getY(), EPSILON);
        assertEquals(0, r, EPSILON);
        assertEquals(1, s, EPSILON);

        // the parent is not affected
        
        p = parent.getGlobalPosition();
        r = parent.getGlobalRotation();
        s = parent.getGlobalScale();
        
        assertEquals(-2, p.getX(), EPSILON);
        assertEquals(3, p.getY(), EPSILON);
        assertEquals(90, r, EPSILON);
        assertEquals(2, s, EPSILON);

    }

    @Test
    public void testSetParent0() {
        Scene scene = new Scene();
        SceneObject obj1 = new SceneObject(scene.getRoot());
        SceneObject obj2 = new SceneObject(scene.getRoot());
        
        assertSame(scene.getRoot(), obj1.getParent());
        assertTrue(obj1.getChildren().isEmpty());
        
        assertSame(scene.getRoot(), obj2.getParent());
        assertTrue(obj2.getChildren().isEmpty());
        
        obj1.translate(1, 1);
        obj1.rotate(90);
        obj1.scale(2);
        
        obj2.setParent(obj1);

        // obj2's global coordinate frame should not be changed
        
        Point2D p = obj2.getGlobalPosition();
        float r = obj2.getGlobalRotation();
        float s = obj2.getGlobalScale();
        
        assertEquals(0, p.getX(), EPSILON);
        assertEquals(0, p.getY(), EPSILON);
        assertEquals(0, r, EPSILON);
        assertEquals(1, s, EPSILON);        

        // obj2's local coordinate frame is adjusted to suit
        
        p = obj2.getPosition();
        r = obj2.getRotation();
        s = obj2.getScale();
        
        assertEquals(-0.5, p.getX(), EPSILON);
        assertEquals(0.5, p.getY(), EPSILON);
        assertEquals(-90, r, EPSILON);
        assertEquals(0.5, s, EPSILON);        

        // obj1's local coordinate frame is not affected
        
        p = obj1.getPosition();
        r = obj1.getRotation();
        s = obj1.getScale();
        
        assertEquals(1, p.getX(), EPSILON);
        assertEquals(1, p.getY(), EPSILON);
        assertEquals(90, r, EPSILON);
        assertEquals(2, s, EPSILON);        

    }
    
    @Test
    public void testSetParent1() {
        Scene scene = new Scene();
    	SceneObject obj1 = new SceneObject(scene.getRoot());
        SceneObject obj2 = new SceneObject(scene.getRoot());
        SceneObject obj3 = new SceneObject(obj1);
        
        assertSame(scene.getRoot(), obj1.getParent());
        assertTrue(!obj1.getChildren().isEmpty());
        
        assertSame(scene.getRoot(), obj2.getParent());
        assertTrue(obj2.getChildren().isEmpty());
        
        obj1.translate(1, 0);
        obj1.rotate(90);
        obj1.scale(1);
        
        obj2.translate(-1, -1);
        obj2.rotate(-45);
        obj2.scale(2);
       
        obj3.translate(1, 0);
        obj3.rotate(60);
        obj3.scale(4);

        //Test obj1
        Point2D gp = obj1.getGlobalPosition();
        float gr = obj1.getGlobalRotation();
        float gs = obj1.getGlobalScale();
                
        Point2D p = obj1.getPosition();
        float r = obj1.getRotation();
        float s = obj1.getScale();
        
        assertEquals(1, gp.getX(), EPSILON);
        assertEquals(0, gp.getY(), EPSILON);
        assertEquals(90, gr, EPSILON);
        assertEquals(1, gs, EPSILON);     
        
        assertEquals(1, p.getX(), EPSILON);
        assertEquals(0, p.getY(), EPSILON);
        assertEquals(90, r, EPSILON);
        assertEquals(1, s, EPSILON);     
        
        //Test obj2
        gp = obj2.getGlobalPosition();
        gr = obj2.getGlobalRotation();
        gs = obj2.getGlobalScale();
                
        p = obj2.getPosition();
        r = obj2.getRotation();
        s = obj2.getScale();
        
        assertEquals(-1, gp.getX(), EPSILON);
        assertEquals(-1, gp.getY(), EPSILON);
        assertEquals(-45, gr, EPSILON);
        assertEquals(2, gs, EPSILON);     
        
        assertEquals(-1, p.getX(), EPSILON);
        assertEquals(-1, p.getY(), EPSILON);
        assertEquals(-45, r, EPSILON);
        assertEquals(2, s, EPSILON);     
        
       
        //Test obj3
        gp = obj3.getGlobalPosition();
        gr = obj3.getGlobalRotation();
        gs = obj3.getGlobalScale();
                
        p = obj3.getPosition();
        r = obj3.getRotation();
        s = obj3.getScale();
        
        assertEquals(1, gp.getX(), EPSILON);
        assertEquals(1, gp.getY(), EPSILON);
        assertEquals(150, gr, EPSILON);
        assertEquals(4, gs, EPSILON);     
        
        assertEquals(1, p.getX(), EPSILON);
        assertEquals(0, p.getY(), EPSILON);
        assertEquals(60, r, EPSILON);
        assertEquals(4, s, EPSILON);     
       
        //Change obj3
        obj3.setParent(obj2);
        
        // obj3's global coordinate frame should not be changed
        gp = obj3.getGlobalPosition();
        gr = obj3.getGlobalRotation();
        gs = obj3.getGlobalScale();
        
        assertEquals(1, gp.getX(), EPSILON);
        assertEquals(1, gp.getY(), EPSILON);
        assertEquals(150, gr, EPSILON);
        assertEquals(4, gs, EPSILON);        

        // obj2's local coordinate frame is adjusted to suit
        
        p = obj3.getPosition();
        r = obj3.getRotation();
        s = obj3.getScale();
        
        assertEquals(0, p.getX(), EPSILON);
        assertEquals(1.41421, p.getY(), EPSILON);
        //195 normalized is -165
        assertEquals(-165, r, EPSILON);
        assertEquals(2, s, EPSILON);        

        // obj1's local coordinate frame is not affected
        
        p = obj1.getPosition();
        r = obj1.getRotation();
        s = obj1.getScale();
        
        assertEquals(1, p.getX(), EPSILON);
        assertEquals(0, p.getY(), EPSILON);
        assertEquals(90, r, EPSILON);
        assertEquals(1, s, EPSILON);      
        
    }
    
}
