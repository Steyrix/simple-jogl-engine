package engine.core.util;

public class DeltaTimeCalculator {

    private long lastTime;

    public DeltaTimeCalculator() {
        lastTime = System.nanoTime();
    }

    public float calcDeltaTime() {
        long time = System.nanoTime();
        float deltaTime = ((time - lastTime) / 1000000f);
        lastTime = time;

        return deltaTime;
    }
}
