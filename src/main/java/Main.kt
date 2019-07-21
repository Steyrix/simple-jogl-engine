import com.jogamp.opengl.GLCapabilities
import com.jogamp.opengl.GLProfile
import com.jogamp.opengl.awt.GLCanvas
import demos.labrynth.GameLabyrinth
import demos.map.MapDemo
import demos.textureArray.TextureArrayDemo
import engine.core.OpenGlContext
import engine.feature.shader.DefaultShaderCreator
import engine.util.updater.ElapsedTimeUpdater

import javax.swing.*

object Main {
    fun main(args: Array<String>) {
        val glProfile = GLProfile.get(GLProfile.GL4)
        val glCapabilities = GLCapabilities(glProfile)

        val glCanvas = GLCanvas(glCapabilities)
        glCanvas.setSize(1280, 720)
        glCanvas.isFocusable = true
        glCanvas.requestFocus()

        //Put your state here
        val basicListener = OpenGlContext(TextureArrayDemo(glCanvas.size, DefaultShaderCreator()),
                ElapsedTimeUpdater(60))

        glCanvas.addGLEventListener(basicListener)
        glCanvas.addKeyListener(basicListener)

        SwingUtilities.invokeLater {
            val mainFrame = JFrame("Simple JOGL game")
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
