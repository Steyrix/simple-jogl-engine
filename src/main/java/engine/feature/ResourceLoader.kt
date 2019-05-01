package engine.feature

class ResourceLoader {
    companion object {
        @JvmStatic
        fun get(relativePath: String): String {
            return javaClass.classLoader.getResource(relativePath).path
        }
    }
}