package engine.feature.shader.implementation

import engine.feature.shader.Shader
import engine.feature.shader.`interface`.ShaderInteractor
import engine.feature.shader.`interface`.ShaderRepository

class DefaultShaderInteractor(private val repo: ShaderRepository) : ShaderInteractor {

    private val updateMap = HashMap<String, () -> Unit>()
    private val activateMap = HashMap<String, () -> Unit>()

    override fun updateShaders() =
            updateMap.forEach {
                it.value.invoke()
            }

    override fun activateShader(shaderId: String) = activateMap[shaderId]!!.invoke()

    override fun setShaderUpdateFunction(shaderId: String, updateFunc: (Shader) -> Unit) {
        updateMap[shaderId] = { updateFunc(getShader(shaderId)) }
    }

    override fun setShaderActivateFunction(shaderId: String, activateFunc: (Shader) -> Unit) {
        activateMap[shaderId] = { activateFunc(getShader(shaderId)) }
    }

    override fun addShader(shaderId: String, shader: Shader) = repo.putShader(shaderId, shader)

    override fun removeShader(shaderId: String) = repo.removeShader(shaderId)

    override fun getShader(shaderId: String): Shader = repo.getShader(shaderId)

    override fun forEach(action: (String) -> Unit) = repo.forEach(action)
}