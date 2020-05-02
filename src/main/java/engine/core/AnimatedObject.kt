package engine.core

import engine.feature.animation.BasicAnimation
import engine.feature.shader.Shader
import engine.feature.shader.ShaderVariableKey

import java.util.ArrayList
import java.util.Collections

class AnimatedObject(private var frameSizeX: Float,
                     private var frameSizeY: Float,
                     vararg animationSet: BasicAnimation) : Entity {

    var animations: ArrayList<BasicAnimation> = ArrayList()
    var currentAnim: BasicAnimation

    init {
        Collections.addAll(this.animations, *animationSet)
        this.currentAnim = this.animations[0]
    }

    fun defineAnimationVariables(graphicalObject: OpenGlObject2D, shader: Shader) {
        if (graphicalObject.isTextured) {
            shader.setFloat(ShaderVariableKey.Anim.xOffset, currentAnim.currentFrameX * frameSizeX, false)
            shader.setInteger(ShaderVariableKey.Anim.xNumber, currentAnim.currentFrameX + 1, false)
            shader.setFloat(ShaderVariableKey.Anim.yOffset, currentAnim.currentFrameY * frameSizeY, false)
            shader.setInteger(ShaderVariableKey.Anim.yNumber, currentAnim.currentFrameY + 1, false)
        }
    }

    fun playAnimation(deltaTime: Float) {
        currentAnim.play(deltaTime)
    }

    fun setAnimation(a: BasicAnimation) {
        if (animations.contains(a)) currentAnim = a
    }

    override fun toString(): String {
        return super.toString() + "\n Animated"
    }

}
