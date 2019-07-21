package engine.feature.animation

import java.util.TreeMap

class BasicAnimation(val name: String, private val animationId: Int, private val usedLayerId: Int,
                     private val framesCountX: Int, private val framesCountY: Int, private val timeLimit: Float) {

    internal var currentFrameX: Int = 0
    internal var currentFrameY: Int = 0
    private var firstPosX: Int = 0
    private var lastPosX: Int = 0
    private val firstPosY: Int
    private val lastPosY: Int
    private var accumulatedTime: Float = 0.toFloat()

    init {

        this.currentFrameX = 1
        this.currentFrameY = 1

        this.firstPosX = 1
        this.firstPosY = 1
        this.lastPosX = framesCountX
        this.lastPosY = framesCountY
        this.accumulatedTime = 0f

        addNewAnim(this)
    }

    fun changeFrame(deltaTime: Float) {
        accumulatedTime += deltaTime
        if (accumulatedTime >= timeLimit) {
            accumulatedTime = 0f

            //System.out.println("X1: " + currentFrameX + " Y1: " + currentFrameY);
            if (framesCountX != 1) {
                if (currentFrameX + 1 > lastPosX) {
                    currentFrameX = firstPosX

                    if (framesCountY != 1) {
                        if (currentFrameY + 1 > lastPosY)
                            currentFrameY = firstPosY
                        else
                            currentFrameY++
                    }
                } else
                    currentFrameX++
            }
        }
        //System.out.println("X2: " + currentFrameX + " Y2:" + currentFrameY);
    }

    fun animForName(animName: String): BasicAnimation? {
        return map[animName]
    }

    fun setFirstPosX(firstPosX: Int) {
        this.firstPosX = firstPosX
    }

    fun setLastPosX(lastPosX: Int) {
        this.lastPosX = lastPosX
    }

    fun setCurrentFrameX(currentFrameX: Int) {
        this.currentFrameX = currentFrameX
    }

    fun setCurrentFrameY(currentFrameY: Int) {
        this.currentFrameY = currentFrameY
    }

    companion object {
        private val map = TreeMap<String, BasicAnimation>()

        fun addNewAnim(a: BasicAnimation) {
            map[a.name] = a
        }
    }
}
