import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import demos.labrynth.GameLabyrinth;
import modules.DeltaTimeCalculator;
import modules.ElapsedTimeUpdater;
import states.GameState;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class OpenGLContext implements GLEventListener, KeyListener {
    private GameState state;
    private ElapsedTimeUpdater elapsedTimeUpdater;
    private DeltaTimeCalculator timer;

    private OpenGLContext(GameState state, int updatePeriod) {
        this.state = state;
        this.elapsedTimeUpdater = new ElapsedTimeUpdater(updatePeriod);
        this.timer = new DeltaTimeCalculator();
    }

    public void init(GLAutoDrawable glAutoDrawable) {
        this.state.init(glAutoDrawable);
        this.elapsedTimeUpdater.resetFunc(() -> {
            this.state.update(timer.calcDeltaTime());
            this.state.display(glAutoDrawable);
        });
    }

    public void dispose(GLAutoDrawable glAutoDrawable) {
        this.state.dispose(glAutoDrawable);
    }

    //TODO: fix deltaTime counting
    public void display(GLAutoDrawable glAutoDrawable) {
        elapsedTimeUpdater.update();
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

    public static void main(String[] args) {
        final GLProfile glProfile = GLProfile.get(GLProfile.GL4);
        GLCapabilities glCapabilities = new GLCapabilities(glProfile);

        final GLCanvas glCanvas = new GLCanvas(glCapabilities);
        glCanvas.setSize(1280, 720);
        glCanvas.setFocusable(true);
        glCanvas.requestFocus();

        //Put your state here
        OpenGLContext basicListener = new OpenGLContext(new GameLabyrinth(glCanvas.getSize()), 60);
        glCanvas.addGLEventListener(basicListener);
        glCanvas.addKeyListener(basicListener);

        SwingUtilities.invokeLater(() -> {
            final JFrame mainFrame = new JFrame("Simple JOGL game");
            mainFrame.getContentPane().add(glCanvas);
            mainFrame.setSize(mainFrame.getContentPane().getPreferredSize());
            mainFrame.setVisible(true);

            mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        });

        while (true) {
            glCanvas.display();
        }
    }

}
