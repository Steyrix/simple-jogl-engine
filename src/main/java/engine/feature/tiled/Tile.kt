package engine.feature.tiled

class Tile(val tileUV: FloatArray) {
    override fun toString(): String {
        return ("Tile UV: " + tileUV.toList().toString())
    }
}