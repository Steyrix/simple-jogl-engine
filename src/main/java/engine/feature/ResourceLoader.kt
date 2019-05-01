package engine.feature

import java.io.File

class ResourceLoader {
    companion object {
        @JvmStatic
        fun getAbsolutePath(relativePath: String): String {
            return javaClass.classLoader.getResource(relativePath).path
        }

        @JvmStatic
        fun getFileFromRelativePath(relativePath: String): File {
            return File(getAbsolutePath(relativePath))
        }

        @JvmStatic
        fun getFileFromAbsolutePath(absolutePath: String): File {
            return File(getAbsolutePath(absolutePath))
        }
    }
}