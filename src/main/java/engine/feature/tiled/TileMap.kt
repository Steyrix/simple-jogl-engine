package engine.feature.tiled

import com.jogamp.opengl.GL4
import engine.feature.shader.Shader
import engine.feature.tiled.property.BooleanProperty
import engine.feature.tiled.property.LayerProperty
import java.io.File
import kotlin.math.roundToInt

//width and height are measured in tiles
class TileMap internal constructor(private val tileLayers: ArrayList<TileLayer>) {

    private var currentWidth = 0f
    private var currentHeight = 0f

    fun draw(gl: GL4, shader: Shader) = tileLayers.forEach { it.draw(gl, shader) }

    fun draw(gl: GL4, xSize: Float, ySize: Float, shader: Shader) {
        currentWidth = xSize
        currentHeight = ySize
        tileLayers.forEach { it.draw(gl, xSize, ySize, shader) }
    }

    fun getTileNumberInLayer(posX: Float, posY: Float, layerNumber: Int = 0): Int {
        val layer = tileLayers[layerNumber]
        val widthInTiles = layer.width

        val currentTileWidth = currentWidth / layer.width
        val currentTileHeight = currentHeight / layer.height

        val xTileNumber = getTiledPosition(currentTileWidth, posX)
        val yTileNumber = getTiledPosition(currentTileHeight, posY)
        return yTileNumber * widthInTiles + xTileNumber
    }

    // TODO: add robustness
    fun getTileProperty(tileNumber: Int, layerNumber: Int = 0, propertyName: String): LayerProperty<out Any> {
        val layer = tileLayers[layerNumber]

        return if (tileNumber != 0) {
            layer.properties.find { it.getName() == propertyName } ?: throw Exception("Property with name $propertyName does not exist")
        } else {
            throw Exception("Zero tile value property obtaining ? (Exception to be reworked)")
        }
    }

    override fun toString(): String {
        return "TileMap. Layers count: " + tileLayers.size
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