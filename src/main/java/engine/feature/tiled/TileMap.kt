package engine.feature.tiled

import com.jogamp.opengl.GL4
import engine.feature.shader.Shader
import kotlin.math.roundToInt

class TileMap(
        private val tileSet: TileSet,
        layers: List<TileLayer>
) {

    companion object {
        private const val NOT_FOUND = -1
    }

    private val layersMap = layers.associateBy({ it.name }, { it })

    val layers: List<TileLayer>
        get() = layersMap.values.toList()

    private var absoluteWidth: Float = 0f
    private var absoluteHeight: Float = 0f

    fun draw(gl: GL4, shader: Shader) {
        layersMap.values.forEach { it.draw(gl, shader) }
    }

    fun draw(gl: GL4, xSize: Float, ySize: Float, shader: Shader) {
        absoluteWidth = xSize
        absoluteHeight = ySize
        layersMap.values.forEach { it.draw(gl, xSize, ySize, shader) }
    }

    fun getTileWidth() = tileSet.relativeTileWidth

    fun getTileHeight() = tileSet.relativeTileHeight

    fun getLayerByName(name: String): TileLayer? = layersMap[name]

    fun getTileIndexInLayer(posX: Float, posY: Float, layerName: String): Int {
        val layer = layersMap[layerName] ?: return NOT_FOUND
        val widthInTiles = layer.widhtInTiles

        val currentTileWidth = absoluteWidth / layer.widhtInTiles
        val currentTileHeight = absoluteHeight / layer.heightInTiles

        val xTileNumber = getTilePosition(currentTileWidth, posX)
        val yTileNumber = getTilePosition(currentTileHeight, posY)
        return yTileNumber * widthInTiles + xTileNumber
    }

    private fun getTilePosition(tileSize: Float, pos: Float): Int {
        val roundedPos = pos.roundToInt()
        if (roundedPos == 0) {
            return 0
        }

        return roundedPos / tileSize.roundToInt()
    }
}