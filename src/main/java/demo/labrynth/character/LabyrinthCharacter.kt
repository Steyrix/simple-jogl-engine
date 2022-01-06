package demo.labrynth.character

import engine.core.*
import engine.core.controllable.ControllableObject
import engine.core.entity.Entity
import engine.feature.collision.BoundingBox
import engine.feature.collision.ColliderEntity
import engine.util.geometry.PointF
import java.awt.event.KeyEvent
import java.util.*
import kotlin.collections.HashSet


// TODO: fucking heavy, refactor - better split/optimize
class LabyrinthCharacter internal constructor(
        posX: Float,
        posY: Float,
        width: Float,
        height: Float,
        animatedObject: AnimatedObject,
        graphicalObject: OpenGlObject2D,
        initialBox: CharacterBoundingBox? = null
) : CompositeObject(animatedObject, null, graphicalObject) {

    private val keyboardItems = hashMapOf<Int, Boolean>()
    private val collisionPoints: ArrayList<PointF> = arrayListOf()

    private var currentBottomPlatform: BoundingBox? = null
    private var isWalking: Boolean = true
    private var canJump: Boolean = true
    private var jumpTime: Float = 0f

    private val xVelocityModifier = 3.5f
    private val yVelocityModifier = 5f
    private val jumpTimeLimit = 600f
    private val deltaGravityModifier = 10
    private val deltaModifier = 20
    private val jumpVelocityMofidier = 25f

    private val controllableObject: ControllableObject = object : ControllableObject() {

        override var shouldCollide: Boolean = true

        override fun reactToCollision(entity: ColliderEntity) {
            if (entity !is BoundingBox || !shouldCollide) return

            if (isBottomContact(entity)) {
                jumpState = false
                currentBottomPlatform = entity
            }
            if (currentBottomPlatform != null && !isBottomContact(currentBottomPlatform!!)) {
                currentBottomPlatform = null
                jumpState = true
            }
            if (entity.containsPoint(true, collisionPoints)) {
                processCollision(entity)
            }
        }

        override fun isControlledByKey(e: KeyEvent) = false

        override fun keyTyped(e: KeyEvent) = Unit

        override fun keyPressed(e: KeyEvent) {

            keyboardItems[e.keyCode] = true

            if (e.keyCode == KeyEvent.VK_D || e.keyCode == KeyEvent.VK_A) {
                if (!isWalking && !jumpState) {
                    setWalkAnim()
                }
            } else {
                if (e.keyCode == KeyEvent.VK_W) {
                    if (!jumpState) setJumpAnim()
                }
            }
        }

        override fun keyReleased(e: KeyEvent) {
            keyboardItems[e.keyCode] = false
        }
    }

    init {
        setCtrlComponent(controllableObject)

        val box = initialBox ?: CharacterBoundingBox(posX, posY, width, height, shouldCollide = true)
        setBoundingBox(box)
        initCollisionPoints(collisionPoints)

        currentBottomPlatform = null
        isWalking = false
        canJump = true
        jumpTime = 0f
    }

    private fun initCollisionPoints(target: ArrayList<PointF>) {
        graphicalComponent.box?.let {
            val points = (it as CharacterBoundingBox).getCollisionPoints()
            target.addAll(points)
        }
    }

    private fun processCollision(anotherBox: BoundingBox) {
        var moveX: Float = graphicalComponent.box!!.getIntersectionWidth(anotherBox)
        var moveY: Float = graphicalComponent.box!!.getIntersectionHeight(anotherBox)
        val horizontalContact = detectHorizontalContact(anotherBox)
        val fallingState = !jumpState && velocityY < 0f

        if (horizontalContact) {
            moveY = 0f
        } else {
            moveX = 0f
        }

        if (velocityX != 0f && velocityY != 0f) {
            velocityX = 0f
            if (fallingState) {
                velocityY = 0f
            }
        } else if (velocityX != 0f) {
            velocityX = 0f
        } else if (velocityY != 0f) {
            velocityY = 0f
        }

        graphicalComponent.box!!.posX += moveX
        graphicalComponent.box!!.posY += moveY
    }

    private fun isBottomContact(anotherBox: BoundingBox): Boolean {
        (graphicalComponent.box as? CharacterBoundingBox)?.let {
            return anotherBox.containsNumberOfPoints(2, false, it.getBottomLinePointSet())
        }

        return false
    }

    private fun detectHorizontalContact(anotherBox: BoundingBox): Boolean {
        (graphicalComponent.box as? CharacterBoundingBox)?.let {
            return anotherBox.containsNumberOfPoints(2, true, it.getLeftBorderPointSet())
                    || anotherBox.containsNumberOfPoints(2, true, it.getRightBorderPointSet())
                    || anotherBox.containsAnyPointOf(true, it.getHorizontalContactPointSet())
        }

        return false
    }

    override fun update(deltaTime: Float) {
        applyVelocityX()
        applyVelocityY()
        processJumpTime(deltaTime)
        processGravityEffect(deltaTime)
        processAnimation()
        changePosition(deltaTime)
        updateCollisionPoints()
        animationComponent!!.playAnimation(deltaTime)
    }

    override fun react(entity: Entity) {
        if (entity is OpenGlObject2D) {
            val box = entity.box
            box?.let { controlComponent?.reactToCollision(it) }
        }
    }

    private fun applyVelocityX() {
        when {
            keyboardItems[KeyEvent.VK_D] == true -> {
                isWalking = true
                velocityX = xVelocityModifier
            }
            keyboardItems[KeyEvent.VK_A] == true -> {
                isWalking = true
                velocityX = -xVelocityModifier
            }
            else -> velocityX = 0f
        }
    }

    private fun applyVelocityY() {
        if (keyboardItems[KeyEvent.VK_S] == true) {
            velocityY = yVelocityModifier
            jumpState = true
            canJump = false
        } else if (keyboardItems[KeyEvent.VK_W] == true && canJump) {
            jump()
        } else if (!jumpState) {
            velocityY = 0.0f
        }
    }

    private fun processJumpTime(deltaTime: Float) {
        if (!canJump) {
            jumpTime += deltaTime
            val limit = jumpTimeLimit
            if (jumpTime >= limit) {
                jumpTime = 0f
                canJump = true
            }
        }
    }

    private fun processGravityEffect(deltaTime: Float) {
        val gravity = 1f
        if (jumpState && currentBottomPlatform == null) {
            velocityY += gravity * deltaTime / deltaGravityModifier
        }
    }

    private fun processAnimation() = with(animationComponent) {
        this?.let {
            val isIdleAnim = velocityX == 0f && velocityY == 0f
            val isWalkAnim = velocityX != 0f && !jumpState && it.currentAnim != it.animations[0]
            val isJumpAnim = velocityX != 0f && jumpState
            val isInsignificantJump = velocityY > 0f && velocityY <= 0.2f

            when {
                isIdleAnim -> setIdleAnim()
                isWalkAnim -> setWalkAnim()
                isJumpAnim -> setJumpAnim()
                isInsignificantJump -> setWalkAnim()
                else -> Unit
            }
        }
    }

    private fun changePosition(deltaTime: Float) = with(graphicalComponent.box) {
        this?.let {
            it.posY += velocityY * deltaTime / deltaModifier
            it.posX += velocityX * deltaTime / deltaModifier
        }
    }

    private fun updateCollisionPoints() {
        graphicalComponent.box?.let {
            val points = (it as CharacterBoundingBox).getCollisionPoints()
            collisionPoints.clear()
            collisionPoints.addAll(points)
        }
    }

    private fun jump() {
        velocityY -= jumpVelocityMofidier
        setAirFloating()
    }

    private fun setAirFloating() {
        jumpState = true
        canJump = false
    }

    // TODO: скорее всего, нужно установить соответствие фреймов и индекса анимации внутри AnimatedObject

    // TODO: a lot of magic numbers - refactor
    private fun setIdleAnim() = animationComponent?.let {
        it.setAnimation(it.animations[2])
        it.currentAnim.setCurrentFrameX(0)
        it.currentAnim.setCurrentFrameY(2)
        isWalking = false
    }

    // TODO: a lot of magic numbers - refactor
    private fun setJumpAnim() = animationComponent?.let {
        it.currentAnim = it.animations[1]
        it.currentAnim.currentFrameY = 1
        it.currentAnim.currentFrameX = 7
        it.currentAnim.setFirstPosX(7)
        it.currentAnim.setLastPosX(9)
    }

    // TODO: a lot of magic numbers - refactor
    private fun setWalkAnim() = animationComponent?.let {
        it.currentAnim = it.animations[0]
        it.currentAnim.currentFrameY = 2
        it.currentAnim.currentFrameX = 1
        it.currentAnim.setFirstPosX(1)
        it.currentAnim.setLastPosX(6)
    }
}