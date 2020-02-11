package engine.feature.animation

import com.jogamp.opengl.GL4
import engine.core.OpenGlObject2D
import engine.feature.shader.Shader
import engine.feature.shader.ShaderVariableKey

import java.awt.*
import java.util.ArrayList
import java.util.Collections

//TODO: make implementation independent from texture/texture_array
open class AnimatedObject : OpenGlObject2D {

    protected var animations: ArrayList<BasicAnimation>
    protected var currentAnim: BasicAnimation

    private var frameSizeX: Float = 0.toFloat()
    private var frameSizeY: Float = 0.toFloat()

    constructor(bufferParamsCount: Int,
                verticesCount: Int,
                gl: GL4,
                boxDim: Dimension,
                id: Int,
                frameSizeX: Float,
                frameSizeY: Float,
                vararg animationSet: BasicAnimation) : super(bufferParamsCount, verticesCount, gl, boxDim, id) {

        this.frameSizeX = frameSizeX
        this.frameSizeY = frameSizeY
        this.animations = ArrayList()

        Collections.addAll(this.animations, *animationSet)
        this.currentAnim = this.animations[0]
    }

    constructor(bufferParamsCount: Int,
                verticesCount: Int,
                gl: GL4,
                posX: Float,
                posY: Float,
                boxDim: Dimension,
                id: Int,
                frameSizeX: Float,
                frameSizeY: Float,
                vararg animationSet: BasicAnimation) : super(bufferParamsCount, verticesCount, gl, posX, posY, boxDim, id) {

        this.frameSizeX = frameSizeX
        this.frameSizeY = frameSizeY
        this.animations = ArrayList()

        Collections.addAll(this.animations, *animationSet)
        this.currentAnim = this.animations[0]
    }

    fun defineAnimationVariables(shader: Shader) {
        if (texture != null || textureArray != null) {
            shader.setFloat(ShaderVariableKey.Anim.xOffset, currentAnim.currentFrameX * frameSizeX, false)
            shader.setInteger(ShaderVariableKey.Anim.xNumber, currentAnim.currentFrameX + 1, false)
            shader.setFloat(ShaderVariableKey.Anim.yOffset, currentAnim.currentFrameY * frameSizeY, false)
            shader.setInteger(ShaderVariableKey.Anim.yNumber, currentAnim.currentFrameY + 1, false)
        }
    }

    protected fun playAnimation(deltaTime: Float) {
        currentAnim.changeFrame(deltaTime)
    }

    protected fun setAnimation(a: BasicAnimation) {
        if (animations.contains(a)) currentAnim = a
    }

    override fun toString(): String {
        return super.toString() + "\n Animated"
    }

}
