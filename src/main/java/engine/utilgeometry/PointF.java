package engine.utilgeometry;

@SuppressWarnings("WeakerAccess")
public class PointF {
    public float x;
    public float y;

    public PointF(){
        this.x = 0f;
        this.y = 0f;
    }

    public PointF(float x, float y){
        this.x = x;
        this.y = y;
    }

    public String toString(){
        return "(x: " + x + " ; " + "y: " + y + " )";
    }
}
