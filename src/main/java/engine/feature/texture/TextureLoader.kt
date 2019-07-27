package engine.feature.texture

import com.jogamp.opengl.*
import com.jogamp.opengl.util.texture.Texture
import com.jogamp.opengl.util.texture.TextureData
import com.jogamp.opengl.util.texture.TextureIO

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.io.*
import java.nio.ByteBuffer
import java.nio.IntBuffer
import java.util.ArrayList

//TODO: implement texture arrays robust support
//TODO: make object
class TextureLoader {

    @Throws(GLException::class, IOException::class)
    fun loadTextureData(filePath: String, gl: GL4): TextureData {
        val imageFile = File(filePath)
        return TextureIO.newTextureData(gl.glProfile, imageFile, true, TextureIO.PNG)
    }

    companion object {
        @Throws(GLException::class, IOException::class)
        fun loadTexture(filePath: String): Texture {
            val outputStream = ByteArrayOutputStream()
            ImageIO.write(ImageIO.read(File(filePath)), "png", outputStream)
            val fileInputStream = ByteArrayInputStream(outputStream.toByteArray())
            return TextureIO.newTexture(fileInputStream, true, TextureIO.PNG)
        }

        //TODO: implement check that textures are same size for avoiding exceptions
        //TODO: fix wrong colors
        fun loadTextureArrayTD(textures: ArrayList<TextureData>,
                               gl: GL4,
                               texLayerWidth: Int,
                               texLayerHeight: Int,
                               repeatable: Boolean,
                               id: Int): IntBuffer {

            val texture = IntBuffer.allocate(1)
            gl.glGenTextures(1, texture)
            gl.glActiveTexture(GL4.GL_TEXTURE0 + id)
            gl.glBindTexture(GL4.GL_TEXTURE_2D_ARRAY, texture.get(0))

            println("loadTextureArray func 0:" + gl.glGetError())

            gl.glTexStorage3D(GL4.GL_TEXTURE_2D_ARRAY, 1, GL4.GL_RGBA8, texLayerWidth, texLayerHeight, textures.size)
            println("loadTextureArray func 1:" + gl.glGetError())

            for ((arraySpot, texData) in textures.withIndex()) {
                println(texData.toString())
                gl.glTexSubImage3D(GL4.GL_TEXTURE_2D_ARRAY, 0, 0, 0, arraySpot, texLayerWidth, texLayerHeight,
                        1, GL4.GL_RGBA, GL4.GL_BYTE, texData.buffer)
            }
            println("loadTextureArray func 2:" + gl.glGetError())

            gl.glTexParameteri(GL4.GL_TEXTURE_2D_ARRAY, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR)
            gl.glTexParameteri(GL4.GL_TEXTURE_2D_ARRAY, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST_MIPMAP_LINEAR)

            if (repeatable) {
                gl.glTexParameteri(GL4.GL_TEXTURE_2D_ARRAY, GL4.GL_TEXTURE_WRAP_S, GL4.GL_REPEAT)
                gl.glTexParameteri(GL4.GL_TEXTURE_2D_ARRAY, GL4.GL_TEXTURE_WRAP_T, GL4.GL_REPEAT)
            } else {
                gl.glTexParameteri(GL4.GL_TEXTURE_2D_ARRAY, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE)
                gl.glTexParameteri(GL4.GL_TEXTURE_2D_ARRAY, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE)
            }

            gl.glBindTexture(GL4.GL_TEXTURE_2D_ARRAY, 0)

            return texture
        }

        private fun getImageDataByte(texImg: BufferedImage): ByteBuffer {
            val pixels = (texImg.raster.dataBuffer as DataBufferByte).data
            return ByteBuffer.wrap(pixels)
        }
    }

}
