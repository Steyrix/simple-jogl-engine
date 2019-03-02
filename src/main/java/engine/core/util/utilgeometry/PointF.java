package engine.core.util.utilgeometry;

@SuppressWarnings("WeakerAccess")
public class PointF {
    public final float x;
    public final float y;

    public PointF(){
        this.x = 0f;
        this.y = 0f;
    }

    public PointF(final float x, final float y){
        this.x = x;
        this.y = y;
    }

    public String toString(){
        return "(x: " + x + " ; " + "y: " + y + " )";
    }
}
