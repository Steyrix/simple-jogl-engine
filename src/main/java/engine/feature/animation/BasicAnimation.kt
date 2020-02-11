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
        currentFrameX = 1
        currentFrameY = 1

        firstPosX = 1
        firstPosY = 1
        lastPosX = framesCountX
        lastPosY = framesCountY
        accumulatedTime = 0f

        addNewAnimToMap(this)
    }

    fun changeFrame(deltaTime: Float) {
        accumulatedTime += deltaTime
        if (accumulatedTime >= timeLimit) {
            accumulatedTime = 0f

            //System.out.println("X1: " + currentFrameX + " Y1: " + currentFrameY);
            if (isMultiframedByX()) {
                if (isLastFrameX()) {
                    currentFrameX = firstPosX

                    if (isMultiframedByY()) {
                        if (isLastFrameY())
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

    private inline fun isLastFrameX() : Boolean = currentFrameX + 1 > lastPosX

    private inline fun isLastFrameY() : Boolean = currentFrameY + 1 > lastPosY

    private inline fun isMultiframedByX() : Boolean = framesCountX != 1

    private inline fun isMultiframedByY() : Boolean = framesCountY != 1

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
        private val map = HashMap<String, BasicAnimation>()

        fun addNewAnimToMap(a: BasicAnimation) {
            map[a.name] = a
        }
    }
}
