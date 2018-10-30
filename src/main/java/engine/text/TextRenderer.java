package engine.text;

import com.jogamp.opengl.util.texture.Texture;
import engine.texture.TextureLoader;
import engine.utilgeometry.PointF;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class TextRenderer {
    private HashMap<Character, PointF> characterCoordinates;
    private Texture textureAtlas;

    public static TextRenderer getRenderer(Dimension charSize, String textureFilePath, ArrayList<Character> characters) {

        Texture textureAtlas = null;
        HashMap<Character, PointF> charsCoords = null;

        try {
            textureAtlas = TextureLoader.loadTexture(textureFilePath);
            charsCoords = generateMap(charSize, textureAtlas, characters);
        } catch (IOException e) {
            System.out.println("IO error. File can not be read");
            e.printStackTrace();
            textureAtlas = null;
        } catch (IllegalArgumentException e){
            System.out.println("Font texture atlas has wrong format");
            e.printStackTrace();
            charsCoords = null;
        }

        return new TextRenderer(textureAtlas, charsCoords);
    }

    public boolean isValid(){
        return this.textureAtlas != null && this.characterCoordinates != null;
    }

    private TextRenderer(Texture textureAtlas, HashMap<Character, PointF> characterCoordinates){
        this.textureAtlas = textureAtlas;
        this.characterCoordinates = characterCoordinates;
    }

    private static HashMap<Character, PointF> generateMap(Dimension charSize, Texture textureAtlas, ArrayList<Character> characters) {
        HashMap<Character, PointF> out = new HashMap<>();
        int xStep, yStep, charsCount;

        if (textureAtlas.getWidth() % charSize.getWidth() == 0 &&
                textureAtlas.getHeight() % charSize.getHeight() == 0) {
            xStep = (int) (textureAtlas.getWidth() / charSize.getWidth());
            yStep = (int) (textureAtlas.getHeight() / charSize.getHeight());
            charsCount = xStep * yStep;
            if (charsCount != characters.size())
                throw new IllegalArgumentException("Invalid texture atlas format");
        } else throw new IllegalArgumentException("Invalid texture atlas format");

        int j = 0, k = 0;

        for (int i = 0; i < charsCount; i++) {
            if (xStep * j > textureAtlas.getWidth()) {
                j = 0;
                k++;
            }
            out.put(characters.get(i), new PointF(xStep * j++, yStep * k));
        }

        return out;
    }
}
