package engine.core.controllable.keyboard

import java.awt.event.KeyEvent

object KeyboardUtils {

    fun getAWSDKeySet() = hashMapOf(
            Pair(KeyEvent.VK_A, false),
            Pair(KeyEvent.VK_W, false),
            Pair(KeyEvent.VK_S, false),
            Pair(KeyEvent.VK_D, false)
    )
}