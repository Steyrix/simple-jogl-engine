package engine.feature.tiled

import com.jogamp.opengl.GL4
import engine.core.OpenGlObject2D
import engine.feature.ResourceLoader
import engine.util.xml.XmlParser
import engine.feature.shader.Shader
import engine.util.geometry.PointF
import org.w3c.dom.Document
import org.w3c.dom.Node
import java.awt.Dimension
import java.io.File

//width and height are measured in tiles
class TileMap internal constructor(private val tileLayers: ArrayList<TileLayer>) {

    fun draw(gl: GL4, shader: Shader) = tileLayers.forEach { it.draw(gl, shader) }

    fun draw(gl: GL4, xSize: Float, ySize: Float, shader: Shader) = tileLayers.forEach { it.draw(gl, xSize, ySize, shader) }

    fun getTile(posX: Float, posY: Float): TileWrapper {
        //TODO: implement
        return TileWrapper(HashMap())
    }

    override fun toString(): String {
        return "Layers count: " + tileLayers.size
    }

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
                    out.addAll(nodes.item(i).textContent.replace("\n", "").split(",").map { it.toInt() - 1 })
                }
            }

            return out
        }
    }
}