package demo.labrynth

import engine.core.*
import engine.feature.collision.BoundingBox
import engine.util.geometry.PointF
import java.awt.event.KeyEvent
import java.util.*

class LabyrinthCharacter internal constructor(
        posX: Float,
        posY: Float,
        width: Float,
        height: Float,
        animatedObject: AnimatedObject,
        graphicalObject: OpenGlObject2D
) : CompositeObject(animatedObject, null, graphicalObject) {

    private val keyboardItems: BooleanArray = BooleanArray(1000000)
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

    private val controllableObject: ControllableObject = object : ControllableObject() {

        override fun reactToCollision(anotherBox: BoundingBox) {
            if (isBottomContact(anotherBox)) {
                jumpState = false
                currentBottomPlatform = anotherBox
            }
            if (currentBottomPlatform != null && !isBottomContact(currentBottomPlatform!!)) {
                currentBottomPlatform = null
                jumpState = true
            }
            if (anotherBox.containsPoint(true, collisionPoints)) {
                processCollision(anotherBox)
            }
        }

        override fun keyTyped(e: KeyEvent) {}

        override fun keyPressed(e: KeyEvent) {
            if (!keyboardItems[e.keyCode]) keyboardItems[e.keyCode] = true
            if (e.keyCode == KeyEvent.VK_D || e.keyCode == KeyEvent.VK_A) {
                if (!isWalking && !jumpState) {
                    setWalkAnim()
                }
            } else if (e.keyCode == KeyEvent.VK_W) if (!jumpState) setJumpAnim()
        }

        override fun keyReleased(e: KeyEvent) {
            if (keyboardItems[e.keyCode]) keyboardItems[e.keyCode] = false
        }
    }

    init {
        setCtrlComponent(controllableObject)
        initCollisionPoints(collisionPoints)

        val box = BoundingBox(posX, posY, width, height)
        setBoundingBox(box)

        currentBottomPlatform = null
        isWalking = false
        canJump = true
        jumpTime = 0f
    }

    private fun initCollisionPoints(target: ArrayList<PointF>) {
        with(graphicalComponent.box) {
            this?.let {
                target.add(PointF(it.posX, it.posY)) // 0
                target.add(PointF(it.posX, it.bottomY)) // 1
                target.add(PointF(it.rightX, it.bottomY)) // 2
                target.add(PointF(it.rightX, it.posY)) // 3
                target.add(PointF(it.posX + it.width / 2, it.posY)) // 4
                target.add(PointF(it.posX + it.width / 2, it.bottomY)) // 5
                target.add(PointF(it.posX, it.posY + it.height / 2)) // 6
                target.add(PointF(it.rightX, it.posY + it.height / 2)) // 7
                target.add(PointF(it.posX, it.posY + it.height / 4)) // 8
                target.add(PointF(it.posX, it.bottomY - it.height / 4)) // 9
                target.add(PointF(it.rightX, it.posY + it.height / 4)) // 10
                target.add(PointF(it.rightX, it.bottomY - it.height / 5)) // 11
                target.add(PointF(it.posX, it.posY + it.height / 3)) // 12
                target.add(PointF(it.rightX, it.posY + it.height / 3)) // 13
                target.add(PointF(it.posX, it.bottomY - it.height / 3)) // 14
                target.add(PointF(it.rightX, it.bottomY - it.height / 3)) // 15
            }
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
        return anotherBox.containsNumberOfPoints(2, false,
                collisionPoints[1],
                collisionPoints[2],
                collisionPoints[5])
    }

    private fun detectHorizontalContact(anotherBox: BoundingBox): Boolean {
        return anotherBox.containsNumberOfPoints(2, true,
                collisionPoints[0],
                collisionPoints[1],
                collisionPoints[6]) ||
                anotherBox.containsNumberOfPoints(2, true,
                        collisionPoints[2],
                        collisionPoints[3],
                        collisionPoints[7]) ||
                anotherBox.containsAnyPointOf(true,
                        collisionPoints[8],
                        collisionPoints[9],
                        collisionPoints[10],
                        collisionPoints[11],
                        collisionPoints[12],
                        collisionPoints[13])
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
            keyboardItems[KeyEvent.VK_D] -> {
                isWalking = true
                velocityX = xVelocityModifier
            }
            keyboardItems[KeyEvent.VK_A] -> {
                isWalking = true
                velocityX = -xVelocityModifier
            }
            else -> velocityX = 0f
        }
    }

    private fun applyVelocityY() {
        if (keyboardItems[KeyEvent.VK_S]) {
            velocityY = yVelocityModifier
            jumpState = true
            canJump = false
        } else if (keyboardItems[KeyEvent.VK_W] && canJump) {
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
        if (jumpState && currentBottomPlatform == null) { //System.out.println("Applying gravity");
            velocityY += gravity * deltaTime / deltaGravityModifier
        }
    }

    private fun processAnimation() = with(animationComponent) {
        this?.let {
            val isIdleAnim = velocityX == 0f && velocityY == 0f
            val isWalkAnim = velocityX != 0f && !jumpState && it.currentAnim != it.animations[0]
            val isJumpAnim = velocityX != 0f && jumpState
            val isInsignificantJump = velocityY > 0f && velocityY <= 0.2f
            val isDiagonalMove = velocityX != 0f && velocityY != 0f

            when {
                isIdleAnim -> setIdleAnim()
                isDiagonalMove -> Unit
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

    private fun updateCollisionPoints() = with(graphicalComponent.box) {
        this?.let {
            collisionPoints[0] = PointF(it.posX, it.posY)
            collisionPoints[1] = PointF(it.posX, it.bottomY)
            collisionPoints[2] = PointF(it.rightX, it.bottomY)
            collisionPoints[3] = PointF(it.rightX, it.posY)
            collisionPoints[4] = PointF(it.posX + it.width / 2, it.posY)
            collisionPoints[5] = PointF(it.posX + it.width / 2, it.bottomY)
            collisionPoints[6] = PointF(it.posX, it.posY + it.height / 2)
            collisionPoints[7] = PointF(it.rightX, it.posY + it.height / 2)
            collisionPoints[8] = PointF(it.posX, it.posY + it.height / 4)
            collisionPoints[9] = PointF(it.posX, it.bottomY - it.height / 4)
            collisionPoints[10] = PointF(it.rightX, it.posY + it.height / 4)
            collisionPoints[11] = PointF(it.rightX, it.bottomY - it.height / 5)
            collisionPoints[12] = PointF(it.posX, it.posY + it.height / 3)
            collisionPoints[13] = PointF(it.rightX, it.posY + it.height / 3)
            collisionPoints[14] = PointF(it.rightX, it.bottomY - it.height / 3)
            collisionPoints[15] = PointF(it.rightX, it.bottomY - it.height / 3)
        }
    }

    private fun jump() {
        velocityY -= 20f
        setAirFloating()
    }

    private fun setAirFloating() {
        jumpState = true
        canJump = false
    }

    private fun setIdleAnim() = animationComponent?.let {
        it.setAnimation(it.animations[2])
        it.currentAnim.setCurrentFrameX(0)
        it.currentAnim.setCurrentFrameY(2)
        isWalking = false
    }

    private fun setJumpAnim() = animationComponent?.let {
        it.currentAnim = it.animations[1]
        it.currentAnim.currentFrameY = 1
        it.currentAnim.currentFrameX = 7
        it.currentAnim.setFirstPosX(7)
        it.currentAnim.setLastPosX(9)
    }

    private fun setWalkAnim() = animationComponent?.let {
        it.currentAnim = it.animations[0]
        it.currentAnim.currentFrameY = 2
        it.currentAnim.currentFrameX = 1
        it.currentAnim.setFirstPosX(1)
        it.currentAnim.setLastPosX(6)
    }
}