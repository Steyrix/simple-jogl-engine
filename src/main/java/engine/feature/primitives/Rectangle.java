package engine.feature.primitives;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.util.texture.Texture;
import engine.core.OpenGlObject;
import engine.core.util.color.ColorUtil;

import java.awt.*;
import java.util.Arrays;


public class Rectangle extends OpenGlObject {

    private static float[] RECTANGLE_BUFFER = new float[]{
            0f, 0f,
            0f, 1f,
            1f, 1f,
            0f, 0f,
            1f, 0f,
            1f, 1f};

    private static int RECTANGLE_BUFFER_PARAMS_COUNT = 2;
    private static int RECTANGLE_VERTICES_COUNT = 6;

    private static String ERR_NOT_VALID_ATTRIB_BUFFER = "The buffer supplied is not valid for rectangle";

    public Rectangle(final GL4 gl, final float width, final float height, final int textureId) {
        super(RECTANGLE_BUFFER_PARAMS_COUNT, RECTANGLE_VERTICES_COUNT, gl, new Dimension((int) width, (int) height), textureId);
    }

    public Rectangle(final GL4 gl, final float posX, final float posY, final float width, final float height, final int textureId) {
        super(RECTANGLE_BUFFER_PARAMS_COUNT, RECTANGLE_VERTICES_COUNT, gl, posX, posY, new Dimension((int) width, (int) height), textureId);
    }

    public void init(final String[] textureFilePaths, final boolean texArray, final float[] attribDataArray) {
        if (isNotValidBufferForRectangle(attribDataArray))
            throw new IllegalArgumentException(ERR_NOT_VALID_ATTRIB_BUFFER);

        super.initRenderData(textureFilePaths, texArray, RECTANGLE_BUFFER, attribDataArray);
    }

    public void init(final Texture texture, final float[] attribDataArray) {
        if (isNotValidBufferForRectangle(attribDataArray))
            throw new IllegalArgumentException(ERR_NOT_VALID_ATTRIB_BUFFER);

        super.initRenderData(texture, RECTANGLE_BUFFER, attribDataArray);
    }

    public void init(final Color color) {
        super.initRenderData(null, RECTANGLE_BUFFER, ColorUtil.getBufferForColor(RECTANGLE_VERTICES_COUNT, color));
    }

    @Override
    public void initRenderData(final String[] textureFilePaths, final boolean texArray, final float[]... dataArrays) {
        validateDataSupplied(dataArrays);
        super.initRenderData(textureFilePaths, texArray, dataArrays);
    }

    @Override
    public void initRenderData(final Texture texture, final float[]... dataArrays) {
        validateDataSupplied(dataArrays);
        super.initRenderData(texture, dataArrays);
    }

    private void validateDataSupplied(float[]... dataArrays) {
        final String ERR_NOT_VALID_BUFFER_COUNT = "Rectangle primitive can only have 2 buffers (vertex and attrib).";
        if (dataArrays.length != RECTANGLE_BUFFER_PARAMS_COUNT)
            throw new IllegalArgumentException(ERR_NOT_VALID_BUFFER_COUNT);

        final String ERR_NOT_VALID_VERTEX_BUFFER = "Rectangular vertex data should be supplied as the first argument for rectangle primitve. \n" +
                "You can use initRectangleRenderData() instead";
        if (!Arrays.equals(RECTANGLE_BUFFER, dataArrays[0]))
            throw new IllegalArgumentException(ERR_NOT_VALID_VERTEX_BUFFER);

        if (isNotValidBufferForRectangle(dataArrays[1]))
            throw new IllegalArgumentException(ERR_NOT_VALID_ATTRIB_BUFFER);
    }

    private boolean isNotValidBufferForRectangle(float[] dataArray) {
        return dataArray.length % 6 != 0;
    }
}
