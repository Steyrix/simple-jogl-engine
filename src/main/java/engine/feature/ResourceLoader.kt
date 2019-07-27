package engine.feature

import java.io.File

object ResourceLoader {
    fun getAbsolutePath(relativePath: String): String {
        return javaClass.classLoader.getResource(relativePath).path
    }

    fun getFileFromRelativePath(relativePath: String): File {
        return File(getAbsolutePath(relativePath))
    }

    fun getFileFromAbsolutePath(absolutePath: String): File {
        return File(getAbsolutePath(absolutePath))
    }
}