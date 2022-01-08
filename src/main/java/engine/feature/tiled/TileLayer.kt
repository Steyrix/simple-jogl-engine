package engine.feature.tiled

import com.jogamp.opengl.GL4
import engine.core.OpenGlObject2D
import engine.feature.shader.Shader
import engine.feature.tiled.property.LayerProperty
import engine.util.geometry.PointF

class TileLayer(
        val name: String,
        val widhtInTiles: Int,
        val heightInTiles: Int,
        private val tileData: MutableList<Int>,
        private val properties: MutableList<LayerProperty<out Any>>,
        private val tileSet: TileSet
) {

    companion object {
        private const val EMPTY_TILE_ID = -1
    }

    val tiles: List<Int>
        get() = tileData.toList()

    private var openGLObject: OpenGlObject2D? = null

    private var originalWidth = 0f
    private var originalHeight = 0f

    private fun getPositionByTileIndex(num: Int): PointF {
        val x: Int = num % widhtInTiles
        val y: Int = num / widhtInTiles

        return PointF(x.toFloat(), y.toFloat())
    }

    private fun toOpenGLObject(gl: GL4): OpenGlObject2D {
        val allVertices: ArrayList<Float> = ArrayList()
        val allUV: ArrayList<Float> = ArrayList()

        for (num in 0 until tileData.size) {
            val pos = getPositionByTileIndex(num)
            val verticesArray = genVertices(pos)

            val tileId = tileData[num]

            if (tileId != EMPTY_TILE_ID) {
                val uvArray = tileSet.getTileById(tileId).tileUV
                allVertices.addAll(verticesArray.toList())
                allUV.addAll(uvArray.toList())
            }
        }

        originalWidth = (widhtInTiles * tileSet.tileWidth).toFloat()
        originalHeight = (heightInTiles * tileSet.tileHeight).toFloat()

        val out = OpenGlObject2D(2, allVertices.size / 2, gl, 0)

        out.initRenderData(tileSet.texture, allVertices.toFloatArray(), allUV.toFloatArray())

        return out
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

    private fun generateDrawableObjectIfNeeded(gl: GL4) {
        if (openGLObject == null) {
            openGLObject = toOpenGLObject(gl)
        }
    }

    fun draw(gl: GL4, shader: Shader) {
        generateDrawableObjectIfNeeded(gl)
        openGLObject!!.draw(0f, 0f, originalWidth, originalHeight, 0f, shader)
    }

    fun draw(gl: GL4, xSize: Float, ySize: Float, shader: Shader) {
        generateDrawableObjectIfNeeded(gl)
        openGLObject!!.draw(0f, 0f, xSize, ySize, 0f, shader)
    }

    fun getProperty(propertyName: String): LayerProperty<out Any>? = properties.find { it.getName() == propertyName }
}