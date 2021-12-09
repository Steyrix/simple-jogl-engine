package engine.core

import com.jogamp.opengl.*
import engine.util.DeltaTimeCalculator
import engine.util.updater.Updater
import engine.core.state.Scene

import java.awt.event.KeyEvent
import java.awt.event.KeyListener

class OpenGlContext(
        private val state: Scene,
        private val updater: Updater
) : GLEventListener, KeyListener {
    private val timer: DeltaTimeCalculator = DeltaTimeCalculator()

    override fun init(glAutoDrawable: GLAutoDrawable) {
        state.init(glAutoDrawable)
        updater.setUpdateFunc {
            state.update(timer.calcDeltaTime())
            state.display(glAutoDrawable)
        }
    }

    override fun dispose(glAutoDrawable: GLAutoDrawable) {
        state.dispose(glAutoDrawable)
    }

    override fun display(glAutoDrawable: GLAutoDrawable) {
        updater.update()
    }

    override fun reshape(glAutoDrawable: GLAutoDrawable, i: Int, i1: Int, i2: Int, i3: Int) {
        state.reshape(glAutoDrawable, i, i1, i2, i3)
    }

    override fun keyTyped(e: KeyEvent) {
        state.keyTyped(e)
    }

    override fun keyPressed(e: KeyEvent) {
        state.keyPressed(e)
    }

    override fun keyReleased(e: KeyEvent) {
        state.keyReleased(e)
    }


}
