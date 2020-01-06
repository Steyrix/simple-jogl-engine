package engine.feature.tiled

import com.jogamp.opengl.GL4
import engine.feature.shader.Shader
import engine.feature.tiled.property.LayerProperty
import java.io.File
import kotlin.math.roundToInt

// width and height are measured in tiles
class TileMap internal constructor(private val tileLayers: ArrayList<TileLayer>) {

    private var currentWidth = 0f
    private var currentHeight = 0f

    fun draw(gl: GL4, shader: Shader) = tileLayers.forEach { it.draw(gl, shader) }

    fun draw(gl: GL4, xSize: Float, ySize: Float, shader: Shader) {
        currentWidth = xSize
        currentHeight = ySize
        tileLayers.forEach { it.draw(gl, xSize, ySize, shader) }
    }

    fun getTileIndexInLayerData(posX: Float, posY: Float, layerIndex: Int = 0): Int {
        val layer = tileLayers[layerIndex]
        val widthInTiles = layer.width

        val currentTileWidth = currentWidth / layer.width
        val currentTileHeight = currentHeight / layer.height

        val xTileNumber = getTiledPosition(currentTileWidth, posX)
        val yTileNumber = getTiledPosition(currentTileHeight, posY)
        return yTileNumber * widthInTiles + xTileNumber
    }

    fun getPropertyValueForTile(tileIndex: Int, layerIndex: Int = 0, propertyName: String): Any? {
        if (!arePropertiesActiveForTile(tileIndex, layerIndex)) return null

        val property = getLayerProperty(layerIndex, propertyName)
        return property.value
    }

    fun getLayerProperty(layerIndex: Int = 0, propertyName: String): LayerProperty<out Any> {
        val layer = getLayer(layerIndex)
        return layer.properties.find { it.getName() == propertyName } ?: throw Exception("Property with name $propertyName does not exist")
    }

    fun arePropertiesActiveForTile(tileIndex: Int, layerIndex: Int = 0): Boolean {
        val layer = getLayer(layerIndex)
        return getTile(tileIndex, layer) != 0
    }

    override fun toString(): String {
        return "TileMap. Layers count: " + tileLayers.size
    }

    private fun getLayer(layerIndex: Int): TileLayer {
        checkLayerExistence(layerIndex)
        return tileLayers[layerIndex]
    }

    private fun getTile(tileIndex: Int, layer: TileLayer): Int {
        checkTileExistence(tileIndex, layer)
        return layer.tileData[tileIndex]
    }

    private fun checkLayerExistence(layerIndex: Int) {
        val incorrectLayerIndex = layerIndex >= tileLayers.size || layerIndex < 0
        if (incorrectLayerIndex) throw Exception("There is no layer with index $layerIndex")
    }

    private fun checkTileExistence(tileIndex: Int, layer: TileLayer) {
        val incorrectTileIndex = tileIndex >= layer.tileData.size || tileIndex < 0
        if (incorrectTileIndex) throw Exception("There is no tile with number $tileIndex in the layer")
    }

    private fun getTiledPosition(tileSize: Float, pos: Float): Int {
        val roundedPos = pos.roundToInt()
        if (roundedPos == 0) return 0

        return roundedPos / tileSize.roundToInt()
    }

    companion object {
        fun createInstance(xmlFile: File): TileMap = TileMapParser.createTileMapFromXml(xmlFile)
    }
}