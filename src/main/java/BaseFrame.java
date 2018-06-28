import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import com.jogamp.opengl.*;

import java.awt.*;

public class BaseFrame implements GLEventListener
{
    private int screenWidth;
    private int screenHeight;

    public BaseFrame(int width, int height){
        this.screenWidth = width;
        this.screenHeight = height;
    }

    public BaseFrame(Dimension dim){
        this.screenWidth = dim.width;
        this.screenHeight = dim.height;
    }

    public void init(GLAutoDrawable glAutoDrawable) {
        Mat4 proj = Matrices.ortho(0,   screenWidth,    screenHeight, 0, 0, 0);
    }

    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    public void display(GLAutoDrawable glAutoDrawable) {
        GL gl = glAutoDrawable.getGL();
    }

    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {

    }
}
