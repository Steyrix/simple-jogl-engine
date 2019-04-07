package engine.core;

import org.jetbrains.annotations.NotNull;

import java.awt.event.KeyEvent;

public interface Controllable {
    void update(float deltaTime);
    void keyTyped(@NotNull KeyEvent e);
    void keyPressed(@NotNull KeyEvent e);
    void keyReleased(@NotNull KeyEvent e);
}
