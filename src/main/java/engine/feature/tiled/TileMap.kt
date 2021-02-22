package engine.feature.tiled

import com.jogamp.opengl.GL4
import engine.feature.shader.Shader
import engine.feature.tiled.property.LayerProperty
import java.io.File
import kotlin.math.roundToInt


class TileMap internal constructor(
        private val tileLayers: MutableList<TileLayer>
) {
    val layers: List<TileLayer>
        get() = tileLayers.toList()

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

    override fun toString(): String {
        return "TileMap. Layers count: " + tileLayers.size
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