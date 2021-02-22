package engine.feature.text.data

import engine.core.OpenGlObject2D

data class Font(
        val name: String,
        val symbolAtlasPath: String,
        val atlasGraphicObject: OpenGlObject2D
)