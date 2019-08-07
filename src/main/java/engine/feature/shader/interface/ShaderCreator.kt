package engine.feature.shader.`interface`

import com.jogamp.opengl.GL4
import engine.feature.shader.Shader

interface ShaderCreator {
    fun create(vertexSource: String, fragmentSource: String, gl: GL4): Shader
    fun create(vertexSource: String, fragmentSource: String, geometrySource: String, gl: GL4): Shader
}
