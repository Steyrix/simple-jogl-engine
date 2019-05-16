package engine.feature.tilemap

import com.jogamp.opengl.GL4
import com.jogamp.opengl.util.texture.Texture
import engine.core.OpenGlObject
import engine.feature.ResourceLoader
import engine.util.xml.XmlParser
import engine.feature.primitives.Rectangle
import engine.feature.shader.Shader
import engine.feature.texture.TextureLoader
import engine.util.utilgeometry.PointF
import org.w3c.dom.Document
import org.w3c.dom.Node
import java.awt.Dimension
import java.io.File

//width and height are measured in tiles
class TileMap internal constructor(private val tileLayers: ArrayList<TileLayer>) {

    companion object {

        private const val MAP = "map"
        private const val MAP_WIDTH = "width"
        private const val MAP_HEIGHT = "height"
        private const val DATA = "data"
        private const val LAYER = "layer"
        const val SOURCE = "source"
        const val TILE_SET = "tileset"

        fun createTileMap(xmlFile: File): TileMap {

            val document = XmlParser.getDocument(xmlFile)
            val mapNode = document!!.getElementsByTagName(MAP)
            val mapNodeAttribs = mapNode.item(0).attributes
            val mapWidth = mapNodeAttribs.getNamedItem(MAP_WIDTH).nodeValue.toInt()
            val mapHeight = mapNodeAttribs.getNamedItem(MAP_HEIGHT).nodeValue.toInt()

            val tileSet = retrieveTileSet(document)

            return TileMap(retrieveLayers(mapWidth, mapHeight, document, tileSet))
        }

        private fun retrieveTileSet(doc: Document): TileSet {

            val tileSetNode = doc.getElementsByTagName(TILE_SET)
            val tileSetAttribs = tileSetNode!!.item(0).attributes
            val tileSetPath = tileSetAttribs.getNamedItem(SOURCE).nodeValue
            val tileSetFile = ResourceLoader.getFileFromRelativePath(tileSetPath)

            return TileSet.createTileSet(tileSetFile)
        }

        private fun retrieveLayers(width: Int, height: Int, doc: Document, tileSet: TileSet): ArrayList<TileLayer> {

            val out = ArrayList<TileLayer>()
            val layers = doc.getElementsByTagName(LAYER)

            for (i in 0 until layers.length) {
                val item = layers.item(i)
                val data = retrieveData(item)

                out.add(TileLayer(width, height, data, tileSet))
            }

            return out
        }

        private fun retrieveData(node: Node): ArrayList<Int> {

            val out = ArrayList<Int>()

            val nodes = node.childNodes
            for (i in 0 until nodes.length) {
                if (nodes.item(i).nodeName == DATA) {
                    out.addAll(nodes.item(i).textContent.replace("\n", "").split(",").map { it.toInt() })
                }
            }

            return out
        }
    }

    fun draw(gl: GL4, shader: Shader) = tileLayers.forEach { it.draw(gl, shader) }

    override fun toString(): String {
        return "Layers count: " + tileLayers.size
    }
}

//TODO(count relative coordinates of vertices for tiles to create a single unite VBO)
internal class TileLayer(private val width: Int,
                         private val height: Int,
                         private val tileData: ArrayList<Int>,
                         private val tileSet: TileSet) {

    private var openGLObject: OpenGlObject? = null

    private fun getPosition(num: Int): PointF {
        val x: Int = num % (width - 1)
        val y: Int = num / (width - 1)

        return PointF(x.toFloat(), y.toFloat())
    }

    private fun genVertices(pos: PointF): FloatArray {
        return floatArrayOf(
                tileSet.relativeTileWidth * pos.x, tileSet.relativeTileHeight * (pos.y + 1),
                tileSet.relativeTileWidth * (pos.x + 1), tileSet.relativeTileHeight * pos.y,
                tileSet.relativeTileWidth * pos.x, tileSet.relativeTileHeight * pos.y,
                tileSet.relativeTileWidth * pos.x, tileSet.relativeTileHeight * (pos.y + 1),
                tileSet.relativeTileWidth * (pos.x + 1), tileSet.relativeTileHeight * (pos.y + 1),
                tileSet.relativeTileWidth * (pos.x + 1), tileSet.relativeTileHeight * pos.y)
    }

    private fun toOpenGLObject(gl: GL4): OpenGlObject {
        val allVertices: ArrayList<Float> = ArrayList()
        val allUV: ArrayList<Float> = ArrayList()

        for (num in 0 until tileData.size) {
            val pos = getPosition(num)
            val verticesArray = genVertices(pos)
            val uvArray = tileSet.getTileById(tileData[num]).arrayUV

            allVertices.addAll(verticesArray.toList())
            allUV.addAll(uvArray.toList())
        }

        val out = OpenGlObject(2, allVertices.size / 2, gl,
                                Dimension(width * tileSet.tileWidth, height * tileSet.tileHeight), 0)

        out.initRenderData(tileSet.texture, allVertices.toFloatArray(), allUV.toFloatArray())

        return out
    }

    fun draw(gl: GL4, shader: Shader) {
        if (openGLObject == null) {
            openGLObject = toOpenGLObject(gl)
        }

        openGLObject!!.draw(0f, shader)
    }
}

internal class TileSet(internal val tileWidth: Int,
                       internal val tileHeight: Int,
                       private val tileCount: Int,
                       private val columnCount: Int,
                       internal val texture: Texture) {

    internal val relativeTileWidth: Float = tileWidth.toFloat() / texture.width.toFloat()
    internal val relativeTileHeight: Float = tileHeight.toFloat() / texture.height.toFloat()

    private val tiles = generateTiles(this)

    private fun generateTileUV(num: Int): FloatArray {
        val rowNumber = num / columnCount
        val columnNumber = num % columnCount

        //TODO("Tile map is parsed upside down, rearrange coordinates or revert map")
        return floatArrayOf(
                columnNumber.toFloat() * relativeTileWidth, rowNumber.toFloat() * relativeTileHeight,
                (columnNumber + 1) * relativeTileWidth, (rowNumber + 1) * relativeTileHeight,
                columnNumber.toFloat() * relativeTileWidth, (rowNumber + 1) * relativeTileHeight,
                columnNumber.toFloat() * relativeTileWidth, rowNumber.toFloat() * relativeTileHeight,
                (columnNumber + 1) * relativeTileWidth, rowNumber.toFloat() * relativeTileHeight,
                (columnNumber + 1) * relativeTileWidth, (rowNumber + 1) * relativeTileHeight)
    }

    fun getTileById(id: Int): Tile {
        return tiles[id]
    }

    companion object {
        private const val TILE_WIDTH = "tilewidth"
        private const val TILE_HEIGHT = "tileheight"
        private const val TILE_COUNT = "tilecount"
        private const val COLUMN_COUNT = "columns"
        private const val IMAGE = "image"

        fun createTileSet(xmlFile: File): TileSet {
            val document = XmlParser.getDocument(xmlFile)!!

            val tileSetNode = document.getElementsByTagName(TileMap.TILE_SET)
            val tileSetAttribs = tileSetNode.item(0).attributes

            val tileWidth = tileSetAttribs.getNamedItem(TILE_WIDTH).nodeValue
            val tileHeight = tileSetAttribs.getNamedItem(TILE_HEIGHT).nodeValue
            val tileCount = tileSetAttribs.getNamedItem(TILE_COUNT).nodeValue
            val columnCount = tileSetAttribs.getNamedItem(COLUMN_COUNT).nodeValue

            val imageNode = document.getElementsByTagName(IMAGE)
            val sourcePath = imageNode.item(0).attributes.getNamedItem(TileMap.SOURCE).nodeValue
            val texture = TextureLoader.loadTexture(ResourceLoader.getAbsolutePath(sourcePath))

            return TileSet(tileWidth.toInt(), tileHeight.toInt(), tileCount.toInt(), columnCount.toInt(), texture)
        }

        fun generateTiles(tileSet: TileSet): ArrayList<Tile> {
            val out: ArrayList<Tile> = ArrayList()

            for (i in 0 until tileSet.tileCount) {
                val uv = tileSet.generateTileUV(i)
                out.add(Tile(uv))
            }

            return out
        }
    }

    //Tile is relative to its tileset
    internal class Tile(private val tileUV: FloatArray) {

        val arrayUV
            get() = tileUV
    }
}