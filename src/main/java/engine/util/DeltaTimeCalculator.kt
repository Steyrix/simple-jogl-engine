package engine.util

class DeltaTimeCalculator {
    private var lastTime = System.nanoTime()

    fun calcDeltaTime(): Float {
        val time = System.nanoTime()
        val deltaTime = (time - lastTime) / 1000000f
        lastTime = time

        return deltaTime
    }
}
