package engine.feature.tiled

class TileWrapper(private val properties: HashMap<String, Boolean>) {
    fun hasProperty(property: String): Boolean {
        return properties[property] ?: false
    }
}