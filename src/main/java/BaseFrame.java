import com.jogamp.opengl.*;
import states.GameState;



public class BaseFrame implements GLEventListener
{

    private GameState state;

    public BaseFrame(GameState state){
        this.state = state;
    }

    public void init(GLAutoDrawable glAutoDrawable) {
        this.state.init(glAutoDrawable);
    }

    public void dispose(GLAutoDrawable glAutoDrawable) {
        this.state.dispose(glAutoDrawable);
    }

    public void display(GLAutoDrawable glAutoDrawable) {
        this.state.display(glAutoDrawable);
    }

    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {
        this.state.reshape(glAutoDrawable, i, i1, i2, i3);
    }
}
