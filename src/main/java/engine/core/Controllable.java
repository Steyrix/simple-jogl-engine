package engine.core;

import java.awt.event.KeyEvent;

public interface Controllable {
    void update(float deltaTime);
    void keyTyped(KeyEvent e);
    void keyPressed(KeyEvent e);
    void keyReleased(KeyEvent e);
}
