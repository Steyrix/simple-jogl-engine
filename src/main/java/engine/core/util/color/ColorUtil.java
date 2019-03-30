package engine.core.util.color;

import java.awt.*;

public class ColorUtil {

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
        if (color == Color.RED) {
            return new float[]{1f, 0f, 0f};
        } else if (color == Color.GREEN) {
            return new float[]{0f, 1f, 0f};
        } else if (color == Color.BLUE) {
            return new float[]{0f, 0f, 1f};
        } else if (color == Color.WHITE) {
            return new float[]{1f, 1f, 1f};
        } else if (color == Color.BLACK) {
            return new float[]{0f, 0f, 0f};
        } else {
            return new float[]{0f, 0f, 0f};
        }
    }
}
