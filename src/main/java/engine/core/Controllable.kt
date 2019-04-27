package engine.core

import java.awt.event.KeyEvent

interface Controllable {
    fun keyTyped(e: KeyEvent)
    fun keyPressed(e: KeyEvent)
    fun keyReleased(e: KeyEvent)
}
