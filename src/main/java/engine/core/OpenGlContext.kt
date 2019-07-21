package engine.core

import com.jogamp.opengl.*
import engine.util.DeltaTimeCalculator
import engine.util.updater.Updater
import engine.core.state.GameState

import java.awt.event.KeyEvent
import java.awt.event.KeyListener

class OpenGlContext(private val state: GameState, private val updater: Updater) : GLEventListener, KeyListener {
    private val timer: DeltaTimeCalculator = DeltaTimeCalculator()

    override fun init(glAutoDrawable: GLAutoDrawable) {
        state.init(glAutoDrawable)
        updater.setUpdateFunc {
            this.state.update(timer.calcDeltaTime())
            this.state.display(glAutoDrawable)
        }
    }

    override fun dispose(glAutoDrawable: GLAutoDrawable) {
        this.state.dispose(glAutoDrawable)
    }

    override fun display(glAutoDrawable: GLAutoDrawable) {
        updater.update()
    }

    override fun reshape(glAutoDrawable: GLAutoDrawable, i: Int, i1: Int, i2: Int, i3: Int) {
        this.state.reshape(glAutoDrawable, i, i1, i2, i3)
    }

    override fun keyTyped(e: KeyEvent) {
        this.state.keyTyped(e)
    }

    override fun keyPressed(e: KeyEvent) {
        this.state.keyPressed(e)
    }

    override fun keyReleased(e: KeyEvent) {
        this.state.keyReleased(e)
    }


}
