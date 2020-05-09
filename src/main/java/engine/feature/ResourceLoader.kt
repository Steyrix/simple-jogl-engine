package engine.feature

import java.io.File

object ResourceLoader {
    fun getAbsolutePath(relativePath: String): String {
        val out = javaClass.classLoader.getResource(relativePath)
        return out?.path ?: throw Exception()
    }

    fun getFileFromRelativePath(relativePath: String) = File(getAbsolutePath(relativePath))

    fun getFileFromAbsolutePath(absolutePath: String) = File(getAbsolutePath(absolutePath))
}