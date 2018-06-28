package engine;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import java.io.File;
import java.io.IOException;

public interface Textured {
    static Texture loadTexture(String texturePath){
        File texFile = new File(texturePath);
        Texture out = null;
        try {
            out = TextureIO.newTexture(texFile,true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out;
    }
}
