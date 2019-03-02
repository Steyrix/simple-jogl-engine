package engine.core.state;

import com.jogamp.opengl.GLAutoDrawable;

import java.awt.event.KeyEvent;

public interface GameState {
    void init(GLAutoDrawable glAutoDrawable);
    void dispose(GLAutoDrawable glAutoDrawable);
    void display(GLAutoDrawable glAutoDrawable);
    void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3);
    void update(float deltaTime);

    //void actionPerformed(ActionEvent e, int deltaTime);
    void keyTyped(KeyEvent e);
    void keyPressed(KeyEvent e);
    void keyReleased(KeyEvent e);
}