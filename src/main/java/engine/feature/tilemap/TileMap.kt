package engine.feature.tilemap

import com.jogamp.opengl.GL4
import engine.feature.primitives.Rectangle
import engine.feature.primitives.Rectangle.RECTANGLE_BUFFER
import engine.feature.shader.Shader
import java.io.File
import java.lang.IllegalArgumentException
import javax.xml.parsers.DocumentBuilderFactory

class Tile(private val width: Float,
           private val height: Float,
           private val texturePath: String,
           private val posX: Float,
           private val posY: Float) {

    private fun toOpenGlObject(gl: GL4): Rectangle {

        val out = Rectangle(gl, width, height, 0)
        out.initRenderData(arrayOf(texturePath), false, RECTANGLE_BUFFER)

        return out
    }

    fun draw(gl: GL4, shader: Shader) {
        toOpenGlObject(gl).draw(posX, posY, width, height, 0f, shader)
    }
}

class TileMap(private val tiles: ArrayList<Tile>) {

    companion object {

        private const val ERROR_MSG_NOT_XML = "The file supplied is not XML!"

        private const val ERROR_MSG_NOT_FOUND = "The file supplied is not found!"

        fun createTileMap(xmlFile: File): TileMap {

            if (!xmlFile.exists()) {
                throw IllegalArgumentException(ERROR_MSG_NOT_FOUND)
            }

            if (xmlFile.extension != "xml") {
                throw IllegalArgumentException(ERROR_MSG_NOT_XML)
            }

            val dbFactory = DocumentBuilderFactory.newInstance()
            val dBuilder = dbFactory.newDocumentBuilder()
            val document = dBuilder.parse(xmlFile)
        }

        private fun createTile(xmlTile: String) {

        }
    }

    fun draw(gl: GL4, shader: Shader) {
        tiles.forEach { it.draw(gl, shader) }
    }
}