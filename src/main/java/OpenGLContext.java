import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import states.GameLabyrinth;
import states.GameState;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class OpenGLContext implements GLEventListener, KeyListener, ActionListener
{
    private GameState state;
    private Timer timer;

    public OpenGLContext(GameState state) {
        this.state = state;
        this.timer = new Timer(50, this);
        this.timer.start();
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

    @Override
    public void actionPerformed(ActionEvent e) {
        this.state.actionPerformed(e);
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
        glCanvas.setSize(1280,720);
        glCanvas.setFocusable(true);
        glCanvas.requestFocus();

        OpenGLContext basicListener = new OpenGLContext(new GameLabyrinth(glCanvas.getSize()));
        glCanvas.addGLEventListener(basicListener);
        glCanvas.addKeyListener(basicListener);

        final Animator animator = new Animator(glCanvas);
        animator.start();

        SwingUtilities.invokeLater(()->{
            final JFrame mainFrame = new JFrame("Simple JOGL game");
            mainFrame.getContentPane().add(glCanvas);
            mainFrame.setSize(mainFrame.getContentPane().getPreferredSize());
            mainFrame.setVisible(true);

            mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        });
    }

}
