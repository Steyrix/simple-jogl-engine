package engine.feature.shader.`interface`

import com.jogamp.opengl.GL4
import engine.feature.shader.Shader

interface ShaderCreator {
    fun attachGl(gl: GL4)
    fun create(vertexSource: String, fragmentSource: String): Shader
    fun create(vertexSource: String, fragmentSource: String, geometrySource: String): Shader
}
