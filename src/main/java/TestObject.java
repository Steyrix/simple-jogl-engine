import com.jogamp.opengl.GL3;
import engine.OpenGlObject;

public class TestObject extends OpenGlObject{

    private float velocityX, velocityY;

    public TestObject(int bufferParamsCount, int verticesCount, GL3 gl) {
        super(bufferParamsCount, verticesCount, gl);
        this.velocityX = 0.0f;
        this.velocityY = 0.0f;
    }

    public void setVelocityX(float value){
        this.velocityX = value;
    }

    public void setVelocityY(float value){
        this.velocityY = value;
    }

    public float getVelocityX() {
        return velocityX;
    }

    public float getVelocityY() {
        return velocityY;
    }
}
