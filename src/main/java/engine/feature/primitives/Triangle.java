package engine.feature.primitives;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.util.texture.Texture;
import engine.core.OpenGlObject;
import engine.core.util.color.ColorUtil;
import engine.core.util.utilgeometry.PointF;

import java.awt.*;
import java.util.Arrays;

public class Triangle extends OpenGlObject implements Primitive {

    private static int TRIANGLE_BUFFER_PARAMS_COUNT = 2;
    private static int TRIANGLE_VERTICES_COUNT = 3;
    private static int TRIANGLE_BUFFER_LENGTH = 6;

    private static String ERR_NOT_VALID_ATTRIB_BUFFER = "The buffer supplied is not valid for triangle";

    public Triangle(final GL4 gl, final Dimension boxDim, final int textureId) {
        super(TRIANGLE_BUFFER_PARAMS_COUNT, TRIANGLE_VERTICES_COUNT, gl, boxDim, textureId);
    }

    public Triangle(final GL4 gl, final float posX, final float posY, final Dimension boxDim, final int textureId) {
        super(TRIANGLE_BUFFER_PARAMS_COUNT, TRIANGLE_VERTICES_COUNT, gl, posX, posY, boxDim, textureId);
    }

    public void init(final Color color, final float[] vertices) {

        if (isBufferValidForPrimitive(vertices) && isValidVerticesForTriangle(vertices)) {
            super.initRenderData(null, vertices, ColorUtil.getBufferForColor(TRIANGLE_VERTICES_COUNT, color));
        } else {
            throw new IllegalArgumentException(ERR_NOT_VALID_ATTRIB_BUFFER);
        }

    }

    public void init(final String[] textureFilePaths, final boolean texArray, final float[] vertices, final float[] attribDataArray) {

        if (isBufferValidForPrimitive(vertices) && isValidVerticesForTriangle(vertices)
                && isBufferValidForPrimitive(attribDataArray)) {
            super.initRenderData(textureFilePaths, texArray, vertices, attribDataArray);
        } else {
            throw new IllegalArgumentException(ERR_NOT_VALID_ATTRIB_BUFFER);
        }
    }

    public void init(final Texture texture, final float[] vertices, final float[] attribDataArray) {

        if (isBufferValidForPrimitive(vertices) && isValidVerticesForTriangle(vertices)
                && isBufferValidForPrimitive(attribDataArray)) {
            super.initRenderData(texture, vertices, attribDataArray);
        } else {
            throw new IllegalArgumentException(ERR_NOT_VALID_ATTRIB_BUFFER);
        }

    }

    @Override
    public void initRenderData(final String[] textureFilePaths, final boolean texArray, final float[]... dataArrays) {
        validateSuppliedData(dataArrays);
        super.initRenderData(textureFilePaths, texArray, dataArrays);
    }

    @Override
    public void initRenderData(final Texture texture, final float[]... dataArrays) {
        validateSuppliedData(dataArrays);
        super.initRenderData(texture, dataArrays);
    }

    @Override
    public void validateSuppliedData(final float[]... dataArrays) {

        final String ERR_NOT_VALID_BUFFER_COUNT = "Triangle primitive can only have 2 buffers (vertex and attrib).";
        if (dataArrays.length != TRIANGLE_BUFFER_PARAMS_COUNT) {
            throw new IllegalArgumentException(ERR_NOT_VALID_BUFFER_COUNT);
        }

        final String ERR_NOT_VALID_VERTEX_BUFFER = "Triangular vertex data should be supplied as the first argument for triangle primitive. \n" +
                "You can use Rectangle.init() instead";
        if (!isBufferValidForPrimitive(dataArrays[0]) || !isValidVerticesForTriangle(dataArrays[0]))
            throw new IllegalArgumentException(ERR_NOT_VALID_VERTEX_BUFFER);

        if (isBufferValidForPrimitive(dataArrays[1]))
            throw new IllegalArgumentException(ERR_NOT_VALID_ATTRIB_BUFFER);
    }

    @Override
    public boolean isBufferValidForPrimitive(float[] buffer) {
        return buffer.length % TRIANGLE_BUFFER_LENGTH == 0;
    }

    private static boolean isValidVerticesForTriangle(float[] buffer) {

        final PointF a = new PointF(buffer[0], buffer[1]);
        final PointF b = new PointF(buffer[2], buffer[3]);
        final PointF c = new PointF(buffer[4], buffer[5]);

        return !a.equals(b) && !a.equals(c) && !b.equals(c);
    }
}
