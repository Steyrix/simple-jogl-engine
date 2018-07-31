package engine.texture;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.IntBuffer;
import java.util.ArrayList;

//TODO: implement texture arrays robust support
public class TextureLoader {
    public static Texture loadTexture (String filePath) throws GLException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(ImageIO.read(new File(filePath)), "png", outputStream);
        InputStream fileInputStream = new ByteArrayInputStream(outputStream.toByteArray());
        return TextureIO.newTexture(fileInputStream, true, TextureIO.PNG);
    }

    public static ArrayList<TextureData> imagesToTextures(ArrayList<BufferedImage> images, GLProfile glProfile) {
        ArrayList<TextureData> out = new ArrayList<>();
        for(BufferedImage i : images)
            out.add(AWTTextureIO.newTextureData(glProfile, i, true));

        return out;
    }

    //TODO: implement check that textures are same size for avoiding exceptions
    //TODO: implement generating mipmaps and different miplevels support
    //TODO: blyaaaaat'
    public static IntBuffer loadTextureArray(ArrayList<TextureData> textures, GL3 gl, int texLayerWidth, int texLayerHeight, int subCount, boolean repeatable) {
        IntBuffer texture = IntBuffer.allocate(1);
        gl.glGenTextures(1, texture);
        gl.glBindTexture(GL3.GL_TEXTURE_2D_ARRAY, texture.get(0));

        gl.glTexStorage3D(GL3.GL_TEXTURE_2D_ARRAY, 1, GL3.GL_RGB8, texLayerWidth, texLayerHeight, textures.size());

        int arraySpot = 0;
        for (TextureData td : textures) {
            gl.glTexSubImage3D(GL3.GL_TEXTURE_2D_ARRAY, 0, 0, 0, arraySpot++, td.getWidth(), td.getHeight(),
                    1, GL.GL_RGB8, GL.GL_UNSIGNED_BYTE, td.getBuffer());
        }

        gl.glGenerateMipmap(GL3.GL_TEXTURE_2D_ARRAY);

        if (repeatable) {
            gl.glTexParameteri(GL3.GL_TEXTURE_2D_ARRAY, GL3.GL_TEXTURE_WRAP_S, GL3.GL_REPEAT);
            gl.glTexParameteri(GL3.GL_TEXTURE_2D_ARRAY, GL3.GL_TEXTURE_WRAP_T, GL3.GL_REPEAT);
        } else {
            gl.glTexParameteri(GL3.GL_TEXTURE_2D_ARRAY, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE);
            gl.glTexParameteri(GL3.GL_TEXTURE_2D_ARRAY, GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE);
        }

        gl.glBindTexture(GL3.GL_TEXTURE_2D_ARRAY, 0);

        return texture;
    }

}
