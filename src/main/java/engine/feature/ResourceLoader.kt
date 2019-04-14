package engine.feature

class ResourceLoader {
    fun get(relativePath: String): String {
        return javaClass.classLoader.getResource(relativePath).path
    }
}