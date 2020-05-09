package engine.feature.tiled

import com.jogamp.opengl.GL4
import engine.feature.shader.Shader
import engine.feature.tiled.property.LayerProperty
import java.io.File
import kotlin.math.roundToInt


class TileMap internal constructor(private val tileLayers: ArrayList<TileLayer>) {

    // width and height are measured in tiles multiplied by their size
    private var currentWidth = 0f
    private var currentHeight = 0f

    fun draw(gl: GL4, shader: Shader) = tileLayers.forEach { it.draw(gl, shader) }

    fun draw(gl: GL4, xSize: Float, ySize: Float, shader: Shader) {
        currentWidth = xSize
        currentHeight = ySize
        tileLayers.forEach { it.draw(gl, xSize, ySize, shader) }
    }

    fun drawTile(gl: GL4, shader: Shader, idx: Int) = tileLayers[idx].draw(gl, shader)

    fun drawTile(gl: GL4, xSize: Float, ySize: Float, shader: Shader, idx: Int) =
            tileLayers[idx].draw(gl, xSize, ySize, shader)

    fun getTileIndexInLayerData(posX: Float, posY: Float, layerIndex: Int = 0): Int {
        val layer = tileLayers[layerIndex]
        val widthInTiles = layer.width

        val currentTileWidth = currentWidth / layer.width
        val currentTileHeight = currentHeight / layer.height

        val xTileNumber = getTiledPosition(currentTileWidth, posX)
        val yTileNumber = getTiledPosition(currentTileHeight, posY)
        return yTileNumber * widthInTiles + xTileNumber
    }

    fun getPropertyValueForTile(tileNumber: Int, layerNumber: Int = 0, propertyName: String): Any? {
        if (!arePropertiesActiveForTile(tileNumber, layerNumber)) return null

        val property = getLayerProperty(layerNumber, propertyName)
        return property.value
    }

    fun getLayerProperty(layerNumber: Int = 0, propertyName: String): LayerProperty<out Any> {
        val layer = getLayer(layerNumber)
        return layer.properties.find { it.getName() == propertyName }
                ?: throw Exception("Property with name $propertyName does not exist")
    }

    fun arePropertiesActiveForTile(tileNumber: Int, layerNumber: Int = 0): Boolean {
        val layer = getLayer(layerNumber)
        return getTile(tileNumber, layer) != EMPTY_TILE_ID
    }

    override fun toString(): String {
        return "TileMap. Layers count: " + tileLayers.size
    }

    private fun getLayer(layerIndex: Int): TileLayer {
        checkLayerExistence(layerIndex)
        return tileLayers[layerIndex]
    }

    private fun getTile(tileNumber: Int, layer: TileLayer): Int {
        checkTileExistence(tileNumber, layer)
        return layer.tileData[tileNumber]
    }

    private fun checkLayerExistence(layerIndex: Int) {
        val incorrectLayerIndex = layerIndex >= tileLayers.size || layerIndex < 0
        if (incorrectLayerIndex) {
            throw Exception("There is no layer with index $layerIndex")
        }
    }

    private fun checkTileExistence(tileIndex: Int, layer: TileLayer) {
        val incorrectTileIndex = tileIndex >= layer.tileData.size || tileIndex < 0
        if (incorrectTileIndex) {
            throw Exception("There is no tile with number $tileIndex in the layer")
        }
    }

    private fun getTiledPosition(tileSize: Float, pos: Float): Int {
        val roundedPos = pos.roundToInt()
        if (roundedPos == 0) {
            return 0
        }

        return roundedPos / tileSize.roundToInt()
    }

    companion object {
        fun createInstance(xmlFile: File): TileMap =
                TiledResourceParser.createTileMapFromXml(xmlFile)

        const val EMPTY_TILE_ID = -1
    }
}