package engine.feature.shader

import com.jogamp.opengl.GL4

interface ShaderCreator {
    fun create(vertexSource: String, fragmentSource: String, gl: GL4): Shader
    fun create(vertexSource: String, fragmentSource: String, geometrySource: String, gl: GL4): Shader
}
