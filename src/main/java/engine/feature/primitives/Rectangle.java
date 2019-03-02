package engine.feature.primitives;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.util.texture.Texture;
import engine.core.OpenGlObject;

import java.awt.*;
import java.util.Arrays;


public class Rectangle extends OpenGlObject {

    private static float[] RECTANGLE_BUFFER = new float[]{
            0f, 1f,
            1f, 0f,
            0f, 0f,
            0f, 1f,
            1f, 1f,
            1f, 0f};

    private static int RECTANGLE_BUFFER_PARAMS_COUNT = 2;
    private static int RECTANGLE_VERTICES_COUNT = 6;

    private static String ERR_NOT_VALID_BUFFER_COUNT = "Rectangle primitive can only have 2 buffers (vertex and attrib).";

    private static String ERR_NOT_VALID_VERTEX_BUFFER =
            "Rectangular vertex data should be supplied as the first argument for rectangle primitve. \n" +
            "You can use initRectangleRenderData() instead";

    private static String ERR_NOT_VALID_ATTRIB_BUFFER = "The buffer supplied is not valid for rectangle";

    public Rectangle (GL4 gl, float width, float height, int textureId) {
        super(RECTANGLE_BUFFER_PARAMS_COUNT, RECTANGLE_VERTICES_COUNT, gl, new Dimension((int)width, (int)height), textureId);
    }

    public Rectangle (GL4 gl, float posX, float posY, float width, float height, int textureId) {
        super(RECTANGLE_BUFFER_PARAMS_COUNT, RECTANGLE_VERTICES_COUNT, gl, posX, posY, new Dimension((int)width, (int)height), textureId);
    }

    public void initRectangleRenderData (String[] textureFilePaths, boolean texArray, float[] attribDataArray) {
        if (!isValidBufferForRectangle(attribDataArray))
            throw new IllegalArgumentException(ERR_NOT_VALID_ATTRIB_BUFFER);

        super.initRenderData(textureFilePaths, texArray, RECTANGLE_BUFFER, attribDataArray);
    }

    public void initRectangleRenderData (Texture texture, float[] attribDataArray) {
        if (!isValidBufferForRectangle(attribDataArray))
            throw new IllegalArgumentException(ERR_NOT_VALID_ATTRIB_BUFFER);

        super.initRenderData(texture, RECTANGLE_BUFFER, attribDataArray);
    }

    @Override
    public void initRenderData(String[] textureFilePaths, boolean texArray, float[]... dataArrays) {
        validateDataSupplied(dataArrays);
        super.initRenderData(textureFilePaths, texArray, dataArrays);
    }

    @Override
    public void initRenderData(Texture texture, float[]... dataArrays) {
        validateDataSupplied(dataArrays);
        super.initRenderData(texture, dataArrays);
    }

    private void validateDataSupplied(float[]... dataArrays) {
        if (dataArrays.length != RECTANGLE_BUFFER_PARAMS_COUNT)
            throw new IllegalArgumentException(ERR_NOT_VALID_BUFFER_COUNT);

        if (!Arrays.equals(RECTANGLE_BUFFER, dataArrays[0]))
            throw new IllegalArgumentException(ERR_NOT_VALID_VERTEX_BUFFER);

        if (!isValidBufferForRectangle(dataArrays[1]))
            throw new IllegalArgumentException(ERR_NOT_VALID_ATTRIB_BUFFER);
    }

    private boolean isValidBufferForRectangle (float[] dataArray) {
        return dataArray.length == 12;
    }
}
