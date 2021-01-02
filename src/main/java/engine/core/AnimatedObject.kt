package engine.core

import engine.feature.animation.BasicAnimation
import engine.feature.shader.Shader
import engine.feature.shader.ShaderVariableKey

// TODO: what if animations list is empty?
class AnimatedObject(
        var frameSizeX: Float,
        var frameSizeY: Float,
        val animations: MutableList<BasicAnimation>) : Entity {

    var currentAnim: BasicAnimation = animations[0]

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
        if (animations.contains(a)) {
            currentAnim = a
        }
    }

    override fun toString()= super.toString() + "\n Animated"


}
