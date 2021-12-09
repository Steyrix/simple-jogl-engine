import com.jogamp.opengl.GLCapabilities
import com.jogamp.opengl.GLProfile
import com.jogamp.opengl.awt.GLCanvas
import demo.labrynth.GameLabyrinth
import demo.map.MapDemo
import engine.core.OpenGlContext
import engine.feature.shader.DefaultShaderCreator
import engine.feature.shader.implementation.DefaultShaderInteractor
import engine.feature.shader.implementation.DefaultShaderRepo
import engine.util.updater.ElapsedTimeUpdater

import javax.swing.*

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        val glProfile = GLProfile.get(GLProfile.GL4)
        val glCapabilities = GLCapabilities(glProfile)

        val glCanvas = GLCanvas(glCapabilities)
        glCanvas.setSize(1280, 720)
        glCanvas.isFocusable = true
        glCanvas.requestFocus()

        val listener = OpenGlContext(
                MapDemo(glCanvas.size, DefaultShaderCreator(), DefaultShaderInteractor(DefaultShaderRepo())),
                ElapsedTimeUpdater(60))

        glCanvas.addGLEventListener(listener)
        glCanvas.addKeyListener(listener)

        SwingUtilities.invokeLater {
            val mainFrame = JFrame("Simple JOGL engine")
            mainFrame.contentPane.add(glCanvas)
            mainFrame.size = mainFrame.contentPane.preferredSize
            mainFrame.isVisible = true
            mainFrame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        }

        while (true) {
            glCanvas.display()
        }
    }
}
