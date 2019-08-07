package engine.feature.shader

import com.jogamp.opengl.GL4
import engine.feature.shader.`interface`.ShaderCreator

import java.io.IOException

class DefaultShaderCreator : ShaderCreator {

    private fun getShaderSource(resourceName: String): String {
        val fileURL = javaClass.classLoader.getResource(resourceName)
        var out = ""

        try {
            out = Shader.readFromFile(fileURL!!.path)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return out
    }

    private fun getShaderBuffer(resourceName: String): Array<String> =
            arrayOf(getShaderSource(resourceName))

    override fun create(vertexResName: String, fragmentResName: String, gl: GL4): Shader {
        val out = Shader(gl)
        out.compile(getShaderBuffer(vertexResName), getShaderBuffer(fragmentResName), null)

        return out
    }

    override fun create(vertexResName: String, fragmentResName: String, geometryResName: String, gl: GL4): Shader {
        val out = Shader(gl)
        out.compile(getShaderBuffer(vertexResName), getShaderBuffer(fragmentResName), getShaderBuffer(geometryResName))

        return out
    }
}
