package demo.labrynth

import com.jogamp.opengl.GL4
import engine.core.OpenGlObject2D
import engine.feature.texture.TextureLoader

import java.awt.*
import java.nio.file.Files
import java.nio.file.Paths
import java.util.ArrayList
import java.util.Scanner
import java.util.stream.Collectors

internal class LabyrinthLevelCreator {
    fun createLevelFromFile(gl: GL4, filePath: String): ArrayList<OpenGlObject2D> {
        val defaultSize = 25
        var vertSize: Int
        var horSize: Int
        var lines: List<String> = ArrayList()
        val outList = ArrayList<OpenGlObject2D>()

        try {
            val path = Paths.get(filePath)
            val regex = "\\[[0-9]+,[0-9]+,[HV],[0-9]+];".toRegex()

            Files.lines(path).use {
                lines = it.filter { line -> line.matches(regex) }.collect(Collectors.toList())
            }
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

    private fun replaceBrackets(l: String) = l.replace("[\\[,\\];]".toRegex(), " ")

    private fun createNewRectObject(startX: Int, startY: Int, horSize: Int, vertSize: Int, gl: GL4) : OpenGlObject2D {
        // If texture is horizontal rectangle spawn height only once, if vertical - spawn width only once
        val floatArrUV = if (horSize >= vertSize) {
            val ratio = (horSize / vertSize).toFloat()
            floatArrayOf(ratio, 0f, 0f, 1f, ratio, 1f, ratio, 0f, 0f, 0f, 0f, 1f)
        } else {
            val ratio = (vertSize / horSize).toFloat()
            floatArrayOf(1f, 0f, 0f, ratio, 1f, ratio, 1f, 0f, 0f, 0f, 0f, ratio)
        }

        return getLevelObject(gl, startX, startY, horSize, vertSize, 6).apply {
                initRenderData(arrayOf(this.javaClass.classLoader.getResource("textures/labyrinth/abbey_base.jpg")!!.path),
                        false,
                        floatArrayOf(0f, 1f, 1f, 0f, 0f, 0f, 0f, 1f, 1f, 1f, 1f, 0f),
                        floatArrUV)
               }
    }


    private fun getLevelObject(gl: GL4, startX: Int, startY: Int, horSize: Int, vertSize: Int, verticesCount: Int) =
            object : OpenGlObject2D(2, verticesCount, gl, startX.toFloat(), startY.toFloat(), Dimension(horSize, vertSize), 0) {
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
