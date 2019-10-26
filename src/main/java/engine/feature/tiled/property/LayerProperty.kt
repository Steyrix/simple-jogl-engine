package engine.feature.tiled.property

interface LayerProperty<T> {
    fun getName(): String
    var value: T
}