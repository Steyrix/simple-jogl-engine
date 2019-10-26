package engine.feature.tiled.property

class StringProperty(private val propertyName: String,
                     private var propertyValue: String) : LayerProperty<String> {

    override fun getName() = propertyName

    override var value: String
        get() = propertyValue
        set(value) {
            propertyValue = value
        }
}