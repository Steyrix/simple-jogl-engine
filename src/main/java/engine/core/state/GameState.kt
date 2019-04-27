package engine.core.state

import com.jogamp.opengl.GLAutoDrawable

import java.awt.event.KeyEvent

interface GameState {
    fun init(glAutoDrawable: GLAutoDrawable)
    fun dispose(glAutoDrawable: GLAutoDrawable)
    fun display(glAutoDrawable: GLAutoDrawable)
    fun reshape(glAutoDrawable: GLAutoDrawable, i: Int, i1: Int, i2: Int, i3: Int)
    fun update(deltaTime: Float)

    //void actionPerformed(ActionEvent e, int deltaTime);
    fun keyTyped(e: KeyEvent)

    fun keyPressed(e: KeyEvent)
    fun keyReleased(e: KeyEvent)
}
