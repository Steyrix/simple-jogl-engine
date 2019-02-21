package engine.animation;

import java.util.Map;
import java.util.TreeMap;

public class BasicAnimation {

    int currentFrameX;
    int currentFrameY;

    private static Map<String, BasicAnimation> map = new TreeMap<>();
    private String animName;

    private int animationId;
    private int usedLayerId;
    private int firstPosX;
    private int lastPosX;
    private int firstPosY;
    private int lastPosY;
    private int framesCountX;
    private int framesCountY;
    private float accumulatedTime;
    private float timeLimit;

    public BasicAnimation(String animName, int animationId, int usedLayerId, int framesCountX, int framesCountY, float timeLimit) {
        this.animName = animName;
        this.animationId = animationId;
        this.usedLayerId = usedLayerId;

        this.framesCountX = framesCountX;
        this.framesCountY = framesCountY;

        this.currentFrameX = 1;
        this.currentFrameY = 1;

        this.firstPosX = 1;
        this.firstPosY = 1;
        this.lastPosX = framesCountX;
        this.lastPosY = framesCountY;

        this.timeLimit = timeLimit;
        this.accumulatedTime = 0f;

        addNewAnim(this);
    }

    public void changeFrame(float deltaTime) {
        accumulatedTime += deltaTime;
        if (accumulatedTime >= timeLimit) {
            accumulatedTime = 0f;

             //System.out.println("X1: " + currentFrameX + " Y1: " + currentFrameY);
            if (framesCountX != 1) {
                if (currentFrameX + 1 > lastPosX) {
                    currentFrameX = firstPosX;

                    if (framesCountY != 1) {
                        if (currentFrameY + 1 > lastPosY)
                            currentFrameY = firstPosY;
                        else
                            currentFrameY++;
                    }
                } else
                    currentFrameX++;
            }
        }
          //System.out.println("X2: " + currentFrameX + " Y2:" + currentFrameY);
    }

    public String getName() {
        return this.animName;
    }

    public static void addNewAnim(BasicAnimation a) {
        if (!map.containsKey(a.getName()))
            map.put(a.getName(), a);
    }

    public BasicAnimation animForName(String animName) {
        return map.get(animName);
    }

    public void setFirstPosX(int firstPosX) {
        this.firstPosX = firstPosX;
    }

    public void setLastPosX(int lastPosX) {
        this.lastPosX = lastPosX;
    }

    public void setCurrentFrameX(int currentFrameX) {
        this.currentFrameX = currentFrameX;
    }

    public void setCurrentFrameY(int currentFrameY) {
        this.currentFrameY = currentFrameY;
    }
}
