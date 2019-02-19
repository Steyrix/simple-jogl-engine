import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import demos.labrynth.GameLabyrinth;
import engine.core.OpenGLContext;
import engine.core.modules.updater.ElapsedTimeUpdater;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        final GLProfile glProfile = GLProfile.get(GLProfile.GL4);
        GLCapabilities glCapabilities = new GLCapabilities(glProfile);

        final GLCanvas glCanvas = new GLCanvas(glCapabilities);
        glCanvas.setSize(1280, 720);
        glCanvas.setFocusable(true);
        glCanvas.requestFocus();

        //Put your state here
        OpenGLContext basicListener = new OpenGLContext(new GameLabyrinth(glCanvas.getSize()),
                                                        new ElapsedTimeUpdater(60));

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
