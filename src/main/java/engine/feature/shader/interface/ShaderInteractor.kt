package engine.feature.shader.`interface`

import engine.feature.shader.Shader

interface ShaderInteractor {
    fun updateShaders()

    fun activateShader(shaderId: String)

    fun setShaderUpdateFunction(shaderId: String, updateFunc: (Shader) -> Unit)
    fun setShaderActivateFunction(shaderId: String, activateFunc: (Shader) -> Unit)

    fun addShader(shaderId: String, shader: Shader)
    fun removeShader(shaderId: String)
    fun getShader(shaderId: String): Shader

    fun forEach(action: (Shader) -> Unit)
}