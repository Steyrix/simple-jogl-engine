package engine.texture;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import javax.imageio.ImageIO;
import java.io.*;

public class TextureLoader {
    public static Texture loadTexture (String filePath) throws GLException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(ImageIO.read(new File(filePath)), "png", outputStream);
        InputStream fileInputStream = new ByteArrayInputStream(outputStream.toByteArray());
        return TextureIO.newTexture(fileInputStream, true, TextureIO.PNG);
    }
}
