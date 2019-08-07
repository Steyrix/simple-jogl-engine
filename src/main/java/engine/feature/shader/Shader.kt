package engine.feature.shader

import com.hackoeur.jglm.Mat4
import com.hackoeur.jglm.Vec3
import com.hackoeur.jglm.Vec4
import com.jogamp.opengl.GL4

import java.io.IOException
import java.nio.ByteBuffer
import java.nio.IntBuffer
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths

class Shader internal constructor(private val gl: GL4) {

    var id: Int = 0
        private set

    fun use() {
        gl.glUseProgram(this.id)
    }

    internal fun compile(vertexShaderSource: Array<String>,
                         fragmentShaderSource: Array<String>,
                         geometryShaderSource: Array<String>?) {

        val sVertex: Int = gl.glCreateShader(GL4.GL_VERTEX_SHADER)
        val sFragment: Int = gl.glCreateShader(GL4.GL_FRAGMENT_SHADER)
        var sGeometry = 0

        gl.glShaderSource(sVertex, 1, vertexShaderSource, null)
        gl.glCompileShader(sVertex)
        checkCompileErrors(sVertex, "VERTEX")

        gl.glShaderSource(sFragment, 1, fragmentShaderSource, null)
        gl.glCompileShader(sFragment)
        checkCompileErrors(sFragment, "FRAGMENT")

        if (geometryShaderSource != null) {
            sGeometry = gl.glCreateShader(GL4.GL_GEOMETRY_SHADER)
            gl.glShaderSource(sGeometry, 1, geometryShaderSource, null)
            gl.glCompileShader(sGeometry)
            checkCompileErrors(sGeometry, "GEOMETRY")
        }

        this.id = gl.glCreateProgram()
        gl.glAttachShader(this.id, sVertex)
        gl.glAttachShader(this.id, sFragment)

        if (geometryShaderSource != null)
            gl.glAttachShader(this.id, sGeometry)

        gl.glLinkProgram(this.id)
        checkCompileErrors(this.id, "PROGRAM")

        gl.glDeleteShader(sVertex)
        gl.glDeleteShader(sFragment)

        if (geometryShaderSource != null)
            gl.glDeleteShader(sGeometry)
    }

    fun setFloat(name: String, value: Float, useShader: Boolean) {
        if (useShader)
            this.use()
        gl.glUniform1f(gl.glGetUniformLocation(this.id, name), value)
    }

    fun setInteger(name: String, value: Int, useShader: Boolean) {
        if (useShader)
            this.use()
        gl.glUniform1i(gl.glGetUniformLocation(this.id, name), value)
    }

    fun setVector2f(name: String, x: Float, y: Float, useShader: Boolean) {
        if (useShader)
            this.use()
        gl.glUniform2f(gl.glGetUniformLocation(this.id, name), x, y)
    }

    fun setVector3f(name: String, x: Float, y: Float, z: Float, useShader: Boolean) {
        if (useShader)
            this.use()
        gl.glUniform3f(gl.glGetUniformLocation(this.id, name), x, y, z)
    }

    fun setVector3f(name: String, value: Vec3, useShader: Boolean) {
        if (useShader)
            this.use()
        gl.glUniform3f(gl.glGetUniformLocation(this.id, name), value.x, value.y, value.z)
    }

    fun setVector4f(name: String, x: Float, y: Float, z: Float, w: Float, useShader: Boolean) {
        if (useShader)
            this.use()
        gl.glUniform4f(gl.glGetUniformLocation(this.id, name), x, y, z, w)
    }

    fun setVector4f(name: String, value: Vec4, useShader: Boolean) {
        if (useShader)
            this.use()
        gl.glUniform4f(gl.glGetUniformLocation(this.id, name), value.x, value.y, value.z, value.w)
    }

    fun setMatrix4f(name: String, value: Mat4, useShader: Boolean) {
        if (useShader)
            this.use()
        gl.glUniformMatrix4fv(gl.glGetUniformLocation(this.id, name), 1, false, value.buffer)
    }

    private fun checkCompileErrors(obj: Int, type: String) {
        val success = IntBuffer.allocate(1)
        val infoLog = ByteBuffer.allocate(1024)

        if (type !== "PROGRAM") {
            gl.glGetShaderiv(obj, GL4.GL_COMPILE_STATUS, success)
            if (success.get(0) <= 0) {
                gl.glGetShaderInfoLog(obj, 1024, null, infoLog)
                println("| ERROR::SHADER: Compile-time error: Type: " + type + "\n"
                        + infoLog + "\n -- --------------------------------------------------- -- ")
            }
        } else {
            gl.glGetProgramiv(obj, GL4.GL_LINK_STATUS, success)
            if (success.get(0) <= 0) {
                gl.glGetProgramInfoLog(obj, 1024, null, infoLog)
                println("| ERROR::SHADER: Link-time error: Type: " + type + "\n"
                        + infoLog + "\n -- --------------------------------------------------- -- ")
            }
        }
    }

    companion object {
        @Throws(IOException::class)
        internal fun readFromFile(filePath: String): String {
            val encoded = Files.readAllBytes(Paths.get(filePath))
            return String(encoded, Charset.defaultCharset())
        }
    }


}
