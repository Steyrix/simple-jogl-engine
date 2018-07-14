package states;

import com.jogamp.opengl.GLAutoDrawable;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public interface GameState {
    void init(GLAutoDrawable glAutoDrawable);
    void dispose(GLAutoDrawable glAutoDrawable);
    void display(GLAutoDrawable glAutoDrawable);
    void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3);

    void actionPerformed(ActionEvent e);
    void keyTyped(KeyEvent e);
    void keyPressed(KeyEvent e);
    void keyReleased(KeyEvent e);
}
