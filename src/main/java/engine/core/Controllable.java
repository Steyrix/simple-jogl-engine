package engine.core;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public interface Controllable {
    void actionPerformed(ActionEvent e);
    void keyTyped(KeyEvent e);
    void keyPressed(KeyEvent e);
    void keyReleased(KeyEvent e);
}
