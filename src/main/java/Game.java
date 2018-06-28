import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;

import javax.swing.*;

public class Game {

    public static void main(String[] args) {
        final GLProfile glProfile = GLProfile.get(GLProfile.GL2);
        GLCapabilities glCapabilities = new GLCapabilities(glProfile);


        final GLCanvas glCanvas = new GLCanvas(glCapabilities);
        glCanvas.setSize(500,500);

        BaseFrame basicListener = new BaseFrame(glCanvas.getSize());
        glCanvas.addGLEventListener(basicListener);


        SwingUtilities.invokeLater(()->{
            final JFrame mainFrame = new JFrame("Simple JOGL game");
            mainFrame.getContentPane().add(glCanvas);
            mainFrame.setSize(mainFrame.getContentPane().getPreferredSize());
            mainFrame.setVisible(true);

            mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        });
    }
}
