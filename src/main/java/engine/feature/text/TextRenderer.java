package engine.feature.text;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.util.texture.Texture;
import engine.core.OpenGlObject;
import engine.feature.shader.Shader;
import engine.feature.texture.TextureLoader;
import engine.core.util.utilgeometry.PointF;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class TextRenderer {
    private HashMap<Character, PointF> characterCoordinates;
    private HashMap<Character, OpenGlObject> cache;
    private Texture textureAtlas;
    private Dimension charSize;

    @NotNull
    public static TextRenderer getRenderer(@NotNull Dimension charSize,
                                           @NotNull String textureFilePath,
                                           @NotNull ArrayList<Character> characters) {
        Texture textureAtlas = null;
        HashMap<Character, PointF> characterCoordinates = null;

        try {
            textureAtlas = TextureLoader.loadTexture(textureFilePath);
            characterCoordinates = generateMap(charSize, textureAtlas, characters);
        } catch (IOException e) {
            System.out.println("IO error. File can not be read");
            e.printStackTrace();
            textureAtlas = null;
        } catch (IllegalArgumentException e) {
            System.out.println("Font texture atlas has wrong format");
            e.printStackTrace();
            characterCoordinates = null;
        }

        return new TextRenderer(textureAtlas, characterCoordinates, charSize);
    }

    public boolean isValid() {
        return this.textureAtlas != null && this.characterCoordinates != null;
    }

    private TextRenderer(@NotNull Texture textureAtlas,
                         @NotNull HashMap<Character, PointF> characterCoordinates,
                         @NotNull Dimension charSize) {
        this.textureAtlas = textureAtlas;
        this.characterCoordinates = characterCoordinates;
        this.charSize = charSize;
        this.cache = new HashMap<>();
    }

    @NotNull
    private static HashMap<Character, PointF> generateMap(@NotNull Dimension charSize,
                                                          @NotNull Texture textureAtlas,
                                                          @NotNull ArrayList<Character> characters) {
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
            if (charSize.getWidth() * j >= textureAtlas.getWidth()) {
                j = 0;
                k++;
            }
            out.put(characters.get(i), new PointF(j++, k));
        }

        return out;
    }

    private void drawCharacter(@NotNull Character c,
                               @NotNull Dimension fontSize,
                               @NotNull GL4 gl,
                               @NotNull PointF pos,
                               @NotNull Shader shader) {
        final OpenGlObject glObject;

        if (!cache.containsKey(c)) {
            glObject = new OpenGlObject(2, 6, gl, pos.x, pos.y,
                    fontSize, 100);

            float[] UVcoordinates = getUV(c);

            glObject.initRenderData(this.textureAtlas,
                    new float[]{0f, 1f,
                            1f, 0f,
                            0f, 0f,
                            0f, 1f,
                            1f, 1f,
                            1f, 0f},
                    UVcoordinates);

            cache.put(c, glObject);
        } else
            glObject = cache.get(c);

        glObject.draw(pos.x, pos.y, (float) fontSize.getWidth(), (float) fontSize.getHeight(), 0f, shader);
    }

    public void drawText(@NotNull String text,
                         @NotNull Dimension fontSize,
                         @NotNull GL4 gl,
                         @NotNull PointF pos,
                         @NotNull Shader shader) {
        int x = 0, y = 0;
        for (Character c : text.toCharArray()) {
            if (c == '\n') {
                y++;
                x = 0;
                continue;
            }

            drawCharacter(c, fontSize, gl,
                    new PointF(pos.x + (float)(fontSize.getWidth() * x++),
                            pos.y + (float)(fontSize.getHeight() * y)),
                    shader);
        }
    }

    private float[] getUV(@NotNull Character c) {
        PointF curr = characterCoordinates.get(c);
        float width = (float) (charSize.getWidth() / textureAtlas.getWidth());
        float height = (float) (charSize.getHeight() / textureAtlas.getHeight());

        return new float[]{
                width * curr.x, height * curr.y,
                width * (curr.x + 1), height * (curr.y + 1),
                width * curr.x, height * (curr.y + 1),
                width * curr.x, height * curr.y,
                width * (curr.x + 1), height * curr.y,
                width * (curr.x + 1), height * (curr.y + 1)
        };
    }

    @Override
    public String toString() {
        return "Text renderer with " + characterCoordinates.size() + " characters. \n" +
                characterCoordinates.toString();
    }
}
