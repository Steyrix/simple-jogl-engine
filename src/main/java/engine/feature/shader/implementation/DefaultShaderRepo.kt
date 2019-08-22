package engine.feature.shader.implementation

import engine.feature.shader.Shader
import engine.feature.shader.`interface`.ShaderRepository

class DefaultShaderRepo : ShaderRepository {

    private val map = HashMap<String, Shader>()

    override fun getShader(shaderId: String): Shader = map[shaderId]!!

    override fun putShader(shaderId: String, shader: Shader) {
        map[shaderId] = shader
    }

    override fun removeShader(shaderId: String) {
        map.remove(shaderId)
    }

    override fun forEach(action: (Shader) -> Unit) {
        map.forEach { action(it.value) }
    }
}