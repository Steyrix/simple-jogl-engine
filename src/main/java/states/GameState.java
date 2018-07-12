package states;

import com.jogamp.opengl.GLAutoDrawable;

public interface GameState {
    void init(GLAutoDrawable glAutoDrawable);
    void dispose(GLAutoDrawable glAutoDrawable);
    void display(GLAutoDrawable glAutoDrawable);
    void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3);
}
