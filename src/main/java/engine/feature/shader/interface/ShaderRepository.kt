package engine.feature.shader.`interface`

import engine.feature.shader.Shader

interface ShaderRepository {
    fun getShader(shaderId: String): Shader
    fun putShader(shaderId: String, shader: Shader)
    fun removeShader(shaderId: String)
    fun forEach(action: (Shader) -> Unit)
}