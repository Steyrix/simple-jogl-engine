package engine.core;

import com.jogamp.opengl.*;
import engine.core.util.DeltaTimeCalculator;
import engine.core.util.updater.Updater;
import engine.core.state.GameState;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class OpenGlContext implements GLEventListener, KeyListener {

    private GameState state;
    private Updater updater;
    private DeltaTimeCalculator timer;

    public OpenGlContext(GameState state, Updater updater) {
        this.state = state;
        this.updater = updater;
        this.timer = new DeltaTimeCalculator();
    }

    public void init(GLAutoDrawable glAutoDrawable) {
        state.init(glAutoDrawable);
        updater.setUpdateFunc(() -> {
            this.state.update(timer.calcDeltaTime());
            this.state.display(glAutoDrawable);
        });
    }

    public void dispose(GLAutoDrawable glAutoDrawable) {
        this.state.dispose(glAutoDrawable);
    }

    public void display(GLAutoDrawable glAutoDrawable) {
        updater.update();
    }

    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {
        this.state.reshape(glAutoDrawable, i, i1, i2, i3);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        this.state.keyTyped(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        this.state.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        this.state.keyReleased(e);
    }



}
