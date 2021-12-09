package engine.core.controllable

import java.awt.event.KeyEvent

interface Controllable {
    fun isControlledByKey(e: KeyEvent): Boolean
    fun keyTyped(e: KeyEvent)
    fun keyPressed(e: KeyEvent)
    fun keyReleased(e: KeyEvent)
}
