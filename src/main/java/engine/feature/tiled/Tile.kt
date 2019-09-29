package engine.feature.tiled

//Tile is relative to its tileset
internal class Tile(private val tileUV: FloatArray) {
    val arrayUV
        get() = tileUV

    override fun toString(): String {
        return ("Tile UV: " + tileUV.toList().toString())
    }
}