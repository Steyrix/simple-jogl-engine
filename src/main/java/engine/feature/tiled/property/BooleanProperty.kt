package engine.feature.tiled.property

class BooleanProperty(
        private val propertyName: String,
        private var propertyValue: Boolean
) : LayerProperty<Boolean> {

    override fun getName() = propertyName

    override var value: Boolean
        get() = propertyValue
        set(value) {
            propertyValue = value
        }
}