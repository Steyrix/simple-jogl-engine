package engine.feature.shader

object ShaderVariableKey {

    object Uni {
        const val textureSample = "textureSample"
        const val textureArray = "textureArray"
    }

    object Mat {
        const val projection = "projection"
        const val model = "model"
    }

    object Anim {
        const val xOffset = "xOffset"
        const val yOffset = "yOffset"
        const val xNumber = "frameNumberX"
        const val yNumber = "frameNumberY"
    }

}