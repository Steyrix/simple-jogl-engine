package engine.util.updater

class ElapsedTimeUpdater(private val updatePeriod: Long) : Updater {
    private var updateFunc: () -> Unit = {}
    private var timeAccumulator: Long = 0

    override fun setUpdateFunc(func: ()->Unit) {
        updateFunc = func
    }

    override fun update() {
        ++timeAccumulator
        execUpdateFuncOnCondition()
    }

    private fun condition(): Boolean {
        return timeAccumulator >= updatePeriod
    }

    private fun execUpdateFuncOnCondition() {
        if (condition()) updateFunc.invoke()
    }
}
