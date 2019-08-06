package engine.feature.texture

import com.jogamp.opengl.*
import com.jogamp.opengl.util.texture.Texture
import com.jogamp.opengl.util.texture.TextureData
import com.jogamp.opengl.util.texture.TextureIO

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.io.*
import java.lang.IllegalArgumentException
import java.nio.ByteBuffer
import java.nio.IntBuffer
import java.util.*

//TODO: implement texture arrays robust support
//TODO: make object
object TextureLoader {

    private const val ILLEGAL_ARRAY_TEXTURE_SOURCE_EXCEPTION = "Illegal array texture source! Must be the same size for all layers."

    @Throws(GLException::class, IOException::class)
    fun loadTextureData(filePath: String, gl: GL4): TextureData {
        val outputStream = ByteArrayOutputStream()
        ImageIO.write(ImageIO.read(File(filePath)), "png", outputStream)
        val fileInputStream = ByteArrayInputStream(outputStream.toByteArray())
        return TextureIO.newTextureData(gl.glProfile, fileInputStream, true, TextureIO.PNG)
    }

    @Throws(GLException::class, IOException::class)
    fun loadTexture(filePath: String): Texture {
        val outputStream = ByteArrayOutputStream()
        ImageIO.write(ImageIO.read(File(filePath)), "png", outputStream)
        val fileInputStream = ByteArrayInputStream(outputStream.toByteArray())
        return TextureIO.newTexture(fileInputStream, true, TextureIO.PNG)
    }

    @Throws(IllegalFormatException::class)
    fun loadTextureArray(gl: GL4, textureId: Int, vararg filePaths: String): IntBuffer {
        val images = ArrayList<TextureData>()
        var width = 0
        var height = 0

        for (path in filePaths) {
            val textureData = loadTextureData(path, gl)

            if (images.isEmpty()) {
                width = textureData.width
                height = textureData.height
            }

            if (textureData.width != width || textureData.height != height) {
                throw IllegalArgumentException(ILLEGAL_ARRAY_TEXTURE_SOURCE_EXCEPTION)
            }

            images.add(textureData)
        }

        return defineArrayTexture(images, gl, width, height, textureId)
    }

    private fun defineArrayTexture(textures: ArrayList<TextureData>,
                                   gl: GL4,
                                   texLayerWidth: Int,
                                   texLayerHeight: Int,
                                   id: Int): IntBuffer {

        val texture = IntBuffer.allocate(1)
        gl.glGenTextures(1, texture)
        gl.glActiveTexture(GL4.GL_TEXTURE0 + id)
        gl.glBindTexture(GL4.GL_TEXTURE_2D_ARRAY, texture.get(0))

        gl.glTexStorage3D(GL4.GL_TEXTURE_2D_ARRAY, 1, GL4.GL_RGBA8, texLayerWidth, texLayerHeight, textures.size)

        for ((arraySpot, texData) in textures.withIndex()) {
            gl.glTexSubImage3D(GL4.GL_TEXTURE_2D_ARRAY, 0, 0, 0, arraySpot, texLayerWidth, texLayerHeight,
                    1, GL4.GL_RGBA, GL4.GL_UNSIGNED_BYTE, texData.buffer)
        }

        gl.glTexParameteri(GL4.GL_TEXTURE_2D_ARRAY, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR)
        gl.glTexParameteri(GL4.GL_TEXTURE_2D_ARRAY, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST_MIPMAP_LINEAR)

        gl.glTexParameteri(GL4.GL_TEXTURE_2D_ARRAY, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE)
        gl.glTexParameteri(GL4.GL_TEXTURE_2D_ARRAY, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE)

        gl.glBindTexture(GL4.GL_TEXTURE_2D_ARRAY, 0)

        return texture
    }
}
