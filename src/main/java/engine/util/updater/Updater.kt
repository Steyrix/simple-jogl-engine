package engine.util.updater

interface Updater {
    fun update()
    fun setUpdateFunc(func: () -> Unit)
}
