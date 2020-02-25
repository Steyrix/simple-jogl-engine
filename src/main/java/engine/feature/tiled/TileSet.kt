package engine.feature.tiled

import com.jogamp.opengl.util.texture.Texture
import engine.feature.ResourceLoader
import engine.feature.texture.TextureLoader
import engine.feature.tiled.TiledResourceParser.SOURCE
import engine.feature.tiled.TiledResourceParser.TILE_SET
import engine.util.xml.XmlParser
import java.io.File

internal class TileSet(internal val tileWidth: Int,
                       internal val tileHeight: Int,
                       internal val texture: Texture,
                       private val tileCount: Int,
                       private val columnCount: Int) {

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

    fun getTileById(id: Int): Tile {
        return tiles[id]
    }

    companion object {
        fun generateTiles(tileSet: TileSet): ArrayList<Tile> {
            val out: ArrayList<Tile> = ArrayList()

            for (i in 0 until tileSet.tileCount) {
                val uv = tileSet.generateTileUV(i)
                out.add(Tile(uv))
            }

            return rotateTiles(out, tileSet)
        }

        private fun rotateTiles(tiles: ArrayList<Tile>, tileSet: TileSet): ArrayList<Tile> {
            val result = ArrayList<Tile>(tiles.size)
            for (i in 0 until tiles.size step tileSet.columnCount) {
                val temporary = ArrayList<Tile>(tileSet.columnCount)
                temporary.addAll(tiles.subList(i, i + tileSet.columnCount))
                temporary.reverse()

                result.addAll(temporary)
            }

            result.reverse()
            return result
        }
    }
}