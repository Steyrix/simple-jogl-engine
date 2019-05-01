package engine.feature.tilemap

import com.jogamp.opengl.GL4
import com.jogamp.opengl.util.texture.Texture
import engine.feature.ResourceLoader
import engine.util.xml.XmlParser
import engine.feature.primitives.Rectangle
import engine.feature.primitives.Rectangle.RECTANGLE_BUFFER
import engine.feature.shader.Shader
import engine.feature.texture.TextureLoader
import engine.util.utilgeometry.PointF
import org.w3c.dom.Document
import java.io.File

private const val SOURCE = "source"
private const val TILE_SET = "tileset"

class Tile(private val width: Float,
           private val height: Float,
           private val texture: Texture,
           private val posX: Float,
           private val posY: Float) {
    
    private fun toOpenGlObject(gl: GL4): Rectangle {

        val out = Rectangle(gl, width, height, 0)
        out.initRenderData(texture, RECTANGLE_BUFFER)

        return out
    }

    fun draw(gl: GL4, shader: Shader) {
        toOpenGlObject(gl).draw(posX, posY, width, height, 0f, shader)
    }
}

class TileMap(private val tiles: ArrayList<Tile>, private val tileSet: TileSet) {

    companion object {

//        fun createTileMap(xmlFile: File): TileMap {
//
//            val document = XmlParser.getDocument(xmlFile)
//            val tileSet = retrieveTileSet(document!!)
//
//            return TileMap(ArrayList())
//        }

        private fun retrieveTileSet(doc: Document) : TileSet {

            val tileSetNode = doc.getElementsByTagName(TILE_SET)
            val tileSetAttribs = tileSetNode!!.item(0).attributes
            val tileSetPath = tileSetAttribs.getNamedItem(SOURCE).nodeValue
            val tileSetFile = ResourceLoader.getFileFromRelativePath(tileSetPath)

            return TileSet.createTileSet(tileSetFile)
        }

        private fun createTile(xmlTile: String) {

        }
    }

    fun draw(gl: GL4, shader: Shader) {
        tiles.forEach { it.draw(gl, shader) }
    }
}

class TileSet(private val tiles: ArrayList<Tile>,
              private val tileWidth: Int,
              private val tileHeight: Int,
              private val tileCount: Int,
              private val columnCount: Int,
              private val texture: Texture) {

    private val relativeTileWidth: Float = tileWidth.toFloat() / texture.width.toFloat()
    private val relativeTileHeight: Float = tileHeight.toFloat() / texture.height.toFloat()
    private val rowCount = tileCount / columnCount

    fun getTilePosition(num: Int) : PointF {
        val rowNumber = num / columnCount
        val columnNumber = num % columnCount
        return PointF(rowNumber * relativeTileWidth, columnNumber * relativeTileHeight)
    }

    companion object {
        private const val TILE_WIDTH = "width"
        private const val TILE_HEIGHT = "height"
        private const val TILE_COUNT = "tilecount"
        private const val COLUMN_COUNT = "columns"
        private const val IMAGE = "image"

        fun createTileSet(xmlFile: File): TileSet {
            val document = XmlParser.getDocument(xmlFile)!!

            val tileSetNode = document.getElementsByTagName(TILE_SET)
            val tileSetAttribs = tileSetNode.item(0).attributes

            val tileWidth = tileSetAttribs.getNamedItem(TILE_WIDTH).nodeValue
            val tileHeight = tileSetAttribs.getNamedItem(TILE_HEIGHT).nodeValue
            val tileCount = tileSetAttribs.getNamedItem(TILE_COUNT).nodeValue
            val columnCount = tileSetAttribs.getNamedItem(COLUMN_COUNT).nodeValue

            val imageNode = document.getElementsByTagName(IMAGE)
            val sourcePath = imageNode.item(0).attributes.getNamedItem(SOURCE).nodeValue
            val texture = TextureLoader.loadTexture(sourcePath)

            return TileSet(ArrayList(), tileWidth.toInt(), tileHeight.toInt(), tileCount.toInt(), columnCount.toInt(), texture)
        }
    }
}