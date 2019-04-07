package engine.feature.tilemap

import com.jogamp.opengl.GL4
import engine.core.util.xml.XmlParser
import engine.feature.primitives.Rectangle
import engine.feature.primitives.Rectangle.RECTANGLE_BUFFER
import engine.feature.shader.Shader
import java.io.File

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

        fun createTileMap(xmlFile: File): TileMap {

            val document = XmlParser.getDocument(xmlFile)


        }

        private fun createTile(xmlTile: String) {

        }
    }

    fun draw(gl: GL4, shader: Shader) {
        tiles.forEach { it.draw(gl, shader) }
    }
}