import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import demos.labrynth.GameLabyrinth;
import states.GameState;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class OpenGLContext implements GLEventListener, KeyListener {
    private GameState state;
    private long lastTime;
    private int updatePeriod = 0;
    private int a = 0;

    private OpenGLContext(GameState state) {
        this.lastTime = System.nanoTime();
        this.state = state;
    }

    public void init(GLAutoDrawable glAutoDrawable) {
        this.state.init(glAutoDrawable);
    }

    public void dispose(GLAutoDrawable glAutoDrawable) {
        this.state.dispose(glAutoDrawable);
    }

    //TODO: fix deltaTime counting
    public void display(GLAutoDrawable glAutoDrawable) {
        if(++updatePeriod > 60) {
            //System.out.println(System.nanoTime() / 1000000000f + " " + ++a);
            this.state.update(this.calcDeltaTime());
            this.state.display(glAutoDrawable);
            updatePeriod = 0;
        }
    }

    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {
        this.state.reshape(glAutoDrawable, i, i1, i2, i3);
    }

    private float calcDeltaTime() {
        long time = System.nanoTime();
        float deltaTime = ((time - lastTime) / 1000000f);
        lastTime = time;

        return deltaTime;
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
        OpenGLContext basicListener = new OpenGLContext(new GameLabyrinth(glCanvas.getSize()));
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
