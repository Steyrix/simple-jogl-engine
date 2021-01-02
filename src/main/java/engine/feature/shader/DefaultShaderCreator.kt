package engine.feature.shader

import com.jogamp.opengl.GL4
import engine.feature.shader.`interface`.ShaderCreator

import java.io.IOException

class DefaultShaderCreator : ShaderCreator {

    private var gl: GL4? = null

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

    private fun getShaderBuffer(resourceName: String): Array<String> = arrayOf(getShaderSource(resourceName))

    override fun attachGl(gl: GL4) {
        this.gl = gl
    }

    override fun create(vertexSource: String, fragmentSource: String): Shader {
        val out = Shader(gl)
        out.compile(getShaderBuffer(vertexSource),
                    getShaderBuffer(fragmentSource),
                    null)

        return out
    }

    override fun create(vertexSource: String, fragmentSource: String, geometrySource: String): Shader {
        val out = Shader(gl)
        out.compile(getShaderBuffer(vertexSource),
                    getShaderBuffer(fragmentSource),
                    getShaderBuffer(geometrySource))

        return out
    }
}
