package engine.feature.tiled

import com.jogamp.opengl.GL4
import engine.core.OpenGlObject2D
import engine.feature.shader.Shader
import engine.feature.tiled.property.LayerProperty
import engine.util.geometry.PointF

class TileLayer(
        internal val width: Int,
        internal val height: Int,
        private val tileData: MutableList<Int>,
        private val properties: MutableList<LayerProperty<out Any>>,
        private val tileSet: TileSet
) {
    val tiles: List<Int>
        get() = tileData.toList()

    private var openGLObject: OpenGlObject2D? = null

    private var glWidth = 0f

    private var glHeight = 0f

    private fun getPosition(num: Int): PointF {
        val x: Int = num % width
        val y: Int = num / width

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

    private fun toOpenGLObject(gl: GL4): OpenGlObject2D {
        val allVertices: ArrayList<Float> = ArrayList()
        val allUV: ArrayList<Float> = ArrayList()

        for (num in 0 until tileData.size) {
            val pos = getPosition(num)
            val verticesArray = genVertices(pos)

            val tileId = tileData[num]

            if (tileId != TileMap.EMPTY_TILE_ID) {
                val uvArray = tileSet.getTileById(tileId).arrayUV
                allVertices.addAll(verticesArray.toList())
                allUV.addAll(uvArray.toList())
            }
        }

        glWidth = (width * tileSet.tileWidth).toFloat()
        glHeight = (height * tileSet.tileHeight).toFloat()

        val out = OpenGlObject2D(2, allVertices.size / 2, gl, 0)

        out.initRenderData(tileSet.texture, allVertices.toFloatArray(), allUV.toFloatArray())

        return out
    }

    private fun checkObject(gl: GL4) {
        if (openGLObject == null) {
            openGLObject = toOpenGLObject(gl)
        }
    }

    fun draw(gl: GL4, shader: Shader) {
        checkObject(gl)
        openGLObject!!.draw(0f, 0f, glWidth, glHeight, 0f, shader)
    }

    fun draw(gl: GL4, xSize: Float, ySize: Float, shader: Shader) {
        checkObject(gl)
        openGLObject!!.draw(0f, 0f, xSize, ySize, 0f, shader)
    }

    fun getProperty(propertyName: String): LayerProperty<out Any>? = properties.find { it.getName() == propertyName }
}