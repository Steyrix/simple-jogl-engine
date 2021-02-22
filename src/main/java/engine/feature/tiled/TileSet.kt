package engine.feature.tiled

import com.jogamp.opengl.util.texture.Texture

class TileSet(
        internal val tileWidth: Int,
        internal val tileHeight: Int,
        internal val texture: Texture,
        private val tileCount: Int,
        private val columnCount: Int
) {

    internal val relativeTileWidth: Float = tileWidth.toFloat() / texture.width.toFloat()
    internal val relativeTileHeight: Float = tileHeight.toFloat() / texture.height.toFloat()

    private val tiles = generateTiles(this)

    private fun generateTileUV(num: Int): FloatArray {
        val rowNumber = num / columnCount
        val columnNumber = num % columnCount

        return floatArrayOf(
                columnNumber.toFloat() * relativeTileWidth, rowNumber.toFloat() * relativeTileHeight,
                (columnNumber + 1) * relativeTileWidth, (rowNumber + 1) * relativeTileHeight,
                columnNumber.toFloat() * relativeTileWidth, (rowNumber + 1) * relativeTileHeight,
                columnNumber.toFloat() * relativeTileWidth, rowNumber.toFloat() * relativeTileHeight,
                (columnNumber + 1) * relativeTileWidth, rowNumber.toFloat() * relativeTileHeight,
                (columnNumber + 1) * relativeTileWidth, (rowNumber + 1) * relativeTileHeight)
    }

    internal fun getTileById(id: Int): MapTile {
        return tiles[id]
    }

    companion object {
        internal fun generateTiles(tileSet: TileSet): MutableList<MapTile> {
            val out: ArrayList<MapTile> = ArrayList()

            for (i in 0 until tileSet.tileCount) {
                val uv = tileSet.generateTileUV(i)
                out.add(MapTile(uv))
            }

            return rotateTiles(out, tileSet)
        }

        private fun rotateTiles(tiles: ArrayList<MapTile>, tileSet: TileSet): ArrayList<MapTile> {
            val result = ArrayList<MapTile>(tiles.size)
            for (i in 0 until tiles.size step tileSet.columnCount) {
                val temporary = ArrayList<MapTile>(tileSet.columnCount)
                temporary.addAll(tiles.subList(i, i + tileSet.columnCount))
                temporary.reverse()

                result.addAll(temporary)
            }

            result.reverse()
            return result
        }
    }
}