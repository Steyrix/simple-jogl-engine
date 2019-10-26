package engine.feature.tiled.property

class IntProperty(private val propertyName: String,
                  private var propertyValue: Int) : LayerProperty<Int> {

    override fun getName() = propertyName

    override var value: Int
        get() = propertyValue
        set(value) {
            propertyValue = value
        }
}