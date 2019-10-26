package engine.feature.tiled

import engine.feature.ResourceLoader
import engine.feature.tiled.property.*
import engine.util.xml.XmlParser
import org.w3c.dom.Document
import org.w3c.dom.Node
import java.io.File
import java.lang.Exception
import java.util.stream.Collectors
import java.util.stream.IntStream

internal object TileMapParser {

    private const val MAP = "map"
    private const val MAP_WIDTH = "width"
    private const val MAP_HEIGHT = "height"
    private const val DATA = "data"

    private const val PROPERTIES = "properties"
    private const val PROPERTY_TYPE = "type"
    private const val PROPERTY_VALUE = "value"
    private const val PROPERTY_NAME = "name"
    private const val BOOL = "bool"
    private const val STRING = "string"
    private const val INT = "int"
    private const val FLOAT = "float"

    private const val LAYER = "layer"

    internal const val SOURCE = "source"
    internal const val TILE_SET = "tileset"

    internal fun createTileMapFromXml(xmlFile: File): TileMap {
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
            val properties = retrieveProperties(item)
            val primitiveProperties = convertToPrimitiveProperties(properties)
            out.add(TileLayer(width, height, data, primitiveProperties, tileSet))
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

    private fun retrieveProperties(node: Node): ArrayList<Node> {
        val out = ArrayList<Node>()

        val nodes = node.childNodes
        for (i in 0 until nodes.length) {
            if (nodes.item(i).nodeName == PROPERTIES) {
                val list = nodes.item(i).childNodes
                out.addAll(IntStream.range(0, list.length)
                        .mapToObj(list::item)
                        .collect(Collectors.toList()))
            }
        }

        return out
    }

    private fun convertToPrimitiveProperties(list: ArrayList<Node>): ArrayList<LayerProperty<out Any>> {
        val out = ArrayList<LayerProperty<out Any>>()

        list.forEach {
            val propertyName = it.attributes.getNamedItem(PROPERTY_NAME).nodeValue!!
            val propertyType = it.attributes.getNamedItem(PROPERTY_TYPE).nodeValue!!
            val propertyValue = it.attributes.getNamedItem(PROPERTY_VALUE).nodeValue!!

            val propertyField = when (propertyType) {
                BOOL -> BooleanProperty(propertyName, propertyValue.toBoolean())
                FLOAT -> FloatProperty(propertyName, propertyValue.toFloat())
                INT -> IntProperty(propertyName, propertyValue.toInt())
                STRING -> StringProperty(propertyName, propertyValue)
                else -> throw Exception()
            }

            out.add(propertyField)
        }

        return out
    }
}
