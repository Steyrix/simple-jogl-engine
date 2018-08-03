package engine.texture;

import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

import javax.imageio.ImageIO;
import java.awt.*;
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
    public static IntBuffer loadTextureArray(ArrayList<TextureData> textures, GL4 gl, int texLayerWidth, int texLayerHeight, boolean repeatable) {
        int id = 7; //Setting up index manually just for testing

        IntBuffer texture = IntBuffer.allocate(1);
        gl.glGenTextures(1, texture);
        gl.glActiveTexture(GL4.GL_TEXTURE0 + 7);
        gl.glBindTexture(GL4.GL_TEXTURE_2D_ARRAY, texture.get(0));
        System.out.println("loadTextureArray func 0:" + gl.glGetError());


        gl.glTexStorage3D(GL4.GL_TEXTURE_2D_ARRAY, 1, GL4.GL_RGBA8, texLayerWidth, texLayerHeight, textures.size());
        System.out.println("loadTextureArray func 1:" + gl.glGetError());

        int arraySpot = 0;
        for (TextureData td : textures) {
            //System.out.println("tdwidth:" + td.getWidth() + ", tdheight:" + td.getHeight());
            gl.glTexSubImage3D(GL4.GL_TEXTURE_2D_ARRAY, 0, 0, 0, arraySpot++, td.getWidth(), td.getHeight(),
                    1, GL4.GL_RGBA, GL4.GL_UNSIGNED_BYTE, td.getBuffer());
        }
        System.out.println("loadTextureArray func 2:" + gl.glGetError());

        gl.glTexParameteri(GL4.GL_TEXTURE_2D_ARRAY, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR);

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

}
