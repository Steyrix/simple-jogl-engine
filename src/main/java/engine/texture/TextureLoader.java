package engine.texture;

import com.jogamp.opengl.*;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

//TODO: implement texture arrays robust support
public class TextureLoader {
    public static Texture loadTexture (String filePath) throws GLException, IOException {
        var outputStream = new ByteArrayOutputStream();
        ImageIO.write(ImageIO.read(new File(filePath)), "png", outputStream);
        InputStream fileInputStream = new ByteArrayInputStream(outputStream.toByteArray());
        return TextureIO.newTexture(fileInputStream, true, TextureIO.PNG);
    }

    public TextureData loadTextureData (String filePath, GL4 gl) throws GLException, IOException {
        var imageFile = new File(filePath);
        return TextureIO.newTextureData(gl.getGLProfile(), imageFile,true, TextureIO.PNG);
    }

    //TODO: implement check that textures are same size for avoiding exceptions
    //TODO: fix wrong colors
    public static IntBuffer loadTextureArrayTD(ArrayList<TextureData> textures, GL4 gl, int texLayerWidth, int texLayerHeight, boolean repeatable, int id) {

        var texture = IntBuffer.allocate(1);
        gl.glGenTextures(1, texture);
        gl.glActiveTexture(GL4.GL_TEXTURE0 + id);
        gl.glBindTexture(GL4.GL_TEXTURE_2D_ARRAY, texture.get(0));

        System.out.println("loadTextureArray func 0:" + gl.glGetError());

        gl.glTexStorage3D(GL4.GL_TEXTURE_2D_ARRAY, 1, GL4.GL_RGBA8, texLayerWidth, texLayerHeight, textures.size());
        System.out.println("loadTextureArray func 1:" + gl.glGetError());

        int arraySpot = 0;
        for (TextureData texData: textures) {
            System.out.println(texData.toString());
            gl.glTexSubImage3D(GL4.GL_TEXTURE_2D_ARRAY, 0, 0, 0, arraySpot++, texLayerWidth, texLayerHeight,
                    1, GL4.GL_RGBA, GL4.GL_BYTE, texData.getBuffer());
        }
        System.out.println("loadTextureArray func 2:" + gl.glGetError());

        gl.glTexParameteri(GL4.GL_TEXTURE_2D_ARRAY, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);
        gl.glTexParameteri(GL4.GL_TEXTURE_2D_ARRAY, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST_MIPMAP_LINEAR);

        if (repeatable) {
            gl.glTexParameteri(GL4.GL_TEXTURE_2D_ARRAY, GL4.GL_TEXTURE_WRAP_S, GL4.GL_REPEAT);
            gl.glTexParameteri(GL4.GL_TEXTURE_2D_ARRAY, GL4.GL_TEXTURE_WRAP_T, GL4.GL_REPEAT);
        } else {
            gl.glTexParameteri(GL4.GL_TEXTURE_2D_ARRAY, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE);
            gl.glTexParameteri(GL4.GL_TEXTURE_2D_ARRAY, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE);
        }

        gl.glBindTexture(GL4.GL_TEXTURE_2D_ARRAY, 0);

        return texture;
    }

    public static IntBuffer loadTextureArray(ArrayList<BufferedImage> textures, GL4 gl, int texLayerWidth, int texLayerHeight, boolean repeatable) {

        var texture = IntBuffer.allocate(1);
        gl.glGenTextures(1, texture);
        gl.glActiveTexture(GL4.GL_TEXTURE0);
        gl.glBindTexture(GL4.GL_TEXTURE_2D_ARRAY, texture.get(0));

        System.out.println("loadTextureArray func 0:" + gl.glGetError());

        gl.glTexStorage3D(GL4.GL_TEXTURE_2D_ARRAY, 1, GL4.GL_RGBA8, texLayerWidth, texLayerHeight, textures.size());
        System.out.println("loadTextureArray func 1:" + gl.glGetError());

        int arraySpot = 0;
        for (BufferedImage texData: textures) {
            gl.glTexSubImage3D(GL4.GL_TEXTURE_2D_ARRAY, 0, 0, 0, arraySpot++, texLayerWidth, texLayerHeight,
                    1, GL4.GL_RGBA, GL4.GL_BYTE, getImageDataByte(texData));
        }
        System.out.println("loadTextureArray func 2:" + gl.glGetError());

        gl.glTexParameteri(GL4.GL_TEXTURE_2D_ARRAY, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);
        gl.glTexParameteri(GL4.GL_TEXTURE_2D_ARRAY, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST_MIPMAP_LINEAR);

        if (repeatable) {
            gl.glTexParameteri(GL4.GL_TEXTURE_2D_ARRAY, GL4.GL_TEXTURE_WRAP_S, GL4.GL_REPEAT);
            gl.glTexParameteri(GL4.GL_TEXTURE_2D_ARRAY, GL4.GL_TEXTURE_WRAP_T, GL4.GL_REPEAT);
        } else {
            gl.glTexParameteri(GL4.GL_TEXTURE_2D_ARRAY, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE);
            gl.glTexParameteri(GL4.GL_TEXTURE_2D_ARRAY, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE);
        }

        gl.glBindTexture(GL4.GL_TEXTURE_2D_ARRAY, 0);

        return texture;
    }

    private static ByteBuffer getImageDataByte(BufferedImage texImg) {
        byte[] pixels = ((DataBufferByte) texImg.getRaster().getDataBuffer()).getData();
        return ByteBuffer.wrap(pixels);
    }

}
