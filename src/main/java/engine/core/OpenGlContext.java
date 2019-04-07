package engine.core;

import com.jogamp.opengl.*;
import engine.core.util.DeltaTimeCalculator;
import engine.core.util.updater.Updater;
import engine.core.state.GameState;
import org.jetbrains.annotations.NotNull;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class OpenGlContext implements GLEventListener, KeyListener {

    private GameState state;
    private final Updater updater;
    private final DeltaTimeCalculator timer;

    public OpenGlContext(@NotNull final GameState state, @NotNull final Updater updater) {
        this.state = state;
        this.updater = updater;
        this.timer = new DeltaTimeCalculator();
    }

    public void init(@NotNull final GLAutoDrawable glAutoDrawable) {
        state.init(glAutoDrawable);
        updater.setUpdateFunc(() -> {
            this.state.update(timer.calcDeltaTime());
            this.state.display(glAutoDrawable);
        });
    }

    public void dispose(@NotNull GLAutoDrawable glAutoDrawable) {
        this.state.dispose(glAutoDrawable);
    }

    public void display(@NotNull GLAutoDrawable glAutoDrawable) {
        updater.update();
    }

    public void reshape(@NotNull GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {
        this.state.reshape(glAutoDrawable, i, i1, i2, i3);
    }

    @Override
    public void keyTyped(@NotNull KeyEvent e) {
        this.state.keyTyped(e);
    }

    @Override
    public void keyPressed(@NotNull KeyEvent e) {
        this.state.keyPressed(e);
    }

    @Override
    public void keyReleased(@NotNull KeyEvent e) {
        this.state.keyReleased(e);
    }



}
