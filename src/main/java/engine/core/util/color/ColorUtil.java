package engine.core.util.color;

import java.awt.*;

public class ColorUtil {

    private static final int CHANNEL_COUNT_RGB = 3;
    private static final int CHANNEL_COUNT_RGBA = 4;

    public static float[] getBufferForColor(int verticesCount, Color color) {

        int colorLineLength = 3;

        float[] out = new float[verticesCount * colorLineLength];

        float[] colorLine = getColorValues(color);
        for (int i = 0; i < out.length; i += colorLineLength) {
            out[i] = colorLine[0];
            out[i + 1] = colorLine[1];
            out[i + 2] = colorLine[2];
            System.out.println(out[i] + ", " + out[i + 1] + ", " + out[i + 2]);
        }
        return out;
    }

    private static float[] getColorValues(Color color) {

        return new float[]{
                color.getRed(),
                color.getGreen(),
                color.getBlue()
        };
    }
}
