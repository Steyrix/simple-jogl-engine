package engine.feature.tiled.property

class FloatProperty(
        private val propertyName: String,
        private var propertyValue: Float
) : LayerProperty<Float> {

    override fun getName() = propertyName

    override var value: Float
        get() = propertyValue
        set(value) {
            propertyValue = value
        }
}