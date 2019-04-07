package engine.util.utilgeometry;

@SuppressWarnings("WeakerAccess")
public class PointF {
    public final float x;
    public final float y;

    public PointF() {
        this.x = 0f;
        this.y = 0f;
    }

    public PointF(final float x, final float y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return "(x: " + x + " ; " + "y: " + y + " )";
    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof PointF) {
            return this.x == ((PointF) o).x && this.y == ((PointF) o).y;
        }

        return false;
    }
}
