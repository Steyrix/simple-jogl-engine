package engine.feature.animation

class BasicAnimation(val name: String, private val animationId: Int, private val usedLayerId: Int,
                     private val framesCountX: Int, private val framesCountY: Int, private val timeLimit: Float) {

    internal var currentFrameX: Int = 1
    internal var currentFrameY: Int = 1

    private var playFunction: ((Float, Int, Int, Int, Int) -> Unit)? = null

    private var lastPosX: Int = framesCountX
    private val lastPosY: Int = framesCountY

    private var firstPosX: Int = 1
    private val firstPosY: Int = 1

    private var accumulatedTime: Float = 0f

    init {
        addNewAnimToMap(this)
    }

    fun play(deltaTime: Float) {

        playFunction?.let {
            it.invoke(deltaTime, currentFrameX, currentFrameY, firstPosX, firstPosY)
            return
        }

        // Default animation sequence
        accumulatedTime += deltaTime
        if (accumulatedTime >= timeLimit) {
            accumulatedTime = 0f

            if (isMultiFramedByX()) {
                if (isLastFrameX()) {
                    currentFrameX = firstPosX

                    if (isMultiFramedByY()) {
                        if (isLastFrameY())
                            currentFrameY = firstPosY
                        else
                            currentFrameY++
                    }
                } else
                    currentFrameX++
            }
        }
    }

    private inline fun isLastFrameX() : Boolean = currentFrameX + 1 > lastPosX

    private inline fun isLastFrameY() : Boolean = currentFrameY + 1 > lastPosY

    private inline fun isMultiFramedByX() : Boolean = framesCountX != 1

    private inline fun isMultiFramedByY() : Boolean = framesCountY != 1

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

    fun setPlayFunction(func: ((Float, Int, Int, Int, Int) -> Unit)?) {
        this.playFunction = func
    }

    companion object {
        private val map = HashMap<String, BasicAnimation>()

        fun addNewAnimToMap(a: BasicAnimation) {
            map[a.name] = a
        }

        fun animForName(animName: String): BasicAnimation? {
            return map[animName]
        }
    }
}
