package demos.labrynth

import com.jogamp.opengl.GL4
import engine.core.OpenGlObject
import engine.feature.texture.TextureLoader

import java.awt.*
import java.nio.file.Files
import java.nio.file.Paths
import java.util.ArrayList
import java.util.Scanner
import java.util.stream.Collectors

internal class LabyrinthLevelCreator {
    fun createLevelFromFile(gl: GL4, filePath: String): ArrayList<OpenGlObject> {
        val defaultSize = 25
        var vertSize: Int
        var horSize: Int
        var lines: List<String> = ArrayList()
        val outList = ArrayList<OpenGlObject>()

        try {
            val path = Paths.get(filePath)
            val regex = "\\[[0-9]+,[0-9]+,[HV],[0-9]+];".toRegex()

            Files.lines(path).use{ lines = it.
                    filter { line -> line.matches(regex) }.
                    collect(Collectors.toList()) }
        } catch (e: Exception) {
            println(e.message)
        }

        lines.forEach {
            val regexed = replaceBrackets(it)
            val scanner = Scanner(regexed)
            val startX = scanner.nextInt()
            val startY = scanner.nextInt()
            val orientation = scanner.next("[HV]")
            val size = scanner.nextInt()

            if (orientation == "H") {
                vertSize = defaultSize
                horSize = size
            } else {
                vertSize = size
                horSize = defaultSize
            }

            outList.add(createNewRectObject(startX, startY, horSize, vertSize, gl))
        }

        return outList
    }

    private fun replaceBrackets(l: String): String {
        return l.replace("[\\[,\\];]".toRegex(), " ")
    }

    private fun createNewRectObject(startX: Int, startY: Int, horSize: Int, vertSize: Int, gl: GL4): OpenGlObject {
        val out = getLevelObject(gl, startX, startY, horSize, vertSize, 6)

        out.initRenderData(arrayOf(this.javaClass.classLoader.getResource("textures/labyrinth/abbey_base.jpg")!!.path),
                false,
                floatArrayOf(0f, 1f, 1f, 0f, 0f, 0f, 0f, 1f, 1f, 1f, 1f, 0f),
                floatArrayOf(10f, 0f, 0f, 10f, 10f, 10f, 10f, 0f, 0f, 0f, 0f, 10f))

        return out
    }

    private fun getLevelObject(gl: GL4, startX: Int, startY: Int, horSize: Int, vertSize: Int, verticesCount: Int): OpenGlObject
            = object : OpenGlObject(2, verticesCount, gl, startX.toFloat(), startY.toFloat(), Dimension(horSize, vertSize), 0)
                {
                    public override fun loadTexture(filePath: String) {
                        try {
                            this.texture = TextureLoader.loadTexture(filePath)
                            GameLabyrinth.initRepeatableTexParameters(this.texture!!, this.gl)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }

}
