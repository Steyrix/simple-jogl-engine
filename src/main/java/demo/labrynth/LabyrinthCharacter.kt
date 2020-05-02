package demo.labrynth

import engine.core.*
import engine.feature.collision.BoundingBox
import engine.util.geometry.PointF
import java.awt.event.KeyEvent
import java.util.*

// TODO: manage collisions with multiple objects at the same time
// TODO: cleanup code
class LabyrinthCharacter internal constructor(posX: Float, posY: Float,
                                              width: Float, height: Float,
                                              animatedObject: AnimatedObject,
                                              graphicalObject: OpenGlObject2D)
    : CompositeObject(animatedObject, null, graphicalObject) {

    private val keyboardItems: BooleanArray = BooleanArray(1000000)
    private val collisionPoints: ArrayList<PointF> = arrayListOf()

    private var currentBottomPlatform: BoundingBox? = null
    private var isWalking: Boolean = true
    private var canJump: Boolean = true
    private var jumpTime: Float = 0f

    private val controllableObject: ControllableObject = object : ControllableObject() {

        override fun reactToCollision(anotherBox: BoundingBox) {
            if (detectBottomContact(anotherBox)) {
                jumpState = false
                currentBottomPlatform = anotherBox
            }
            if (currentBottomPlatform != null && !detectBottomContact(currentBottomPlatform!!)) {
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
            } else if (e.keyCode == KeyEvent.VK_W) if (!jumpState) setJumpAnimation()
        }

        override fun keyReleased(e: KeyEvent) {
            if (keyboardItems[e.keyCode]) keyboardItems[e.keyCode] = false
            when (e.keyCode) {
                KeyEvent.VK_D -> {
                }
                KeyEvent.VK_A -> {
                }
                KeyEvent.VK_W -> {
                }
                KeyEvent.VK_S -> {
                }
                else -> {
                }
            }
        }
    }

    init {
        setControl(controllableObject)
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
                target.add(PointF(posX, posY))
                target.add(PointF(posX, bottomY))
                target.add(PointF(rightX, bottomY))
                target.add(PointF(rightX, posY))
                target.add(PointF(posX + width / 2, posY))
                target.add(PointF(posX + width / 2, bottomY))
                target.add(PointF(posX, posY + height / 2))
                target.add(PointF(rightX, posY + height / 2))
                target.add(PointF(posX, posY + height / 4)) // 8
                target.add(PointF(posX, bottomY - height / 4)) // 9
                target.add(PointF(rightX, posY + height / 4)) // 10
                target.add(PointF(rightX, bottomY - height / 5)) // 11
            }
        }
    }

    private fun processCollision(anotherBox: BoundingBox) {

        var moveX: Float = graphicalComponent.box!!.getIntersectionWidth(anotherBox)
        var moveY: Float = graphicalComponent.box!!.getIntersectionHeight(anotherBox)
        val horizontalContact = detectHorizontalContact(anotherBox)
        val fallingState = !jumpState && velocityY < 0f

        if (horizontalContact) moveY = 0f else moveX = 0f

        if (velocityX != 0f && velocityY != 0f) {
            velocityX = 0f
            if (fallingState) velocityY = 0f
        } else if (velocityX != 0f) {
            velocityX = 0f
        } else if (velocityY != 0f) {
            velocityY = 0f
        }

        graphicalComponent.box!!.posX += moveX
        graphicalComponent.box!!.posY += moveY
    }

    private fun detectBottomContact(anotherBox: BoundingBox): Boolean {
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
                        collisionPoints[11])
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
            box?.let { controllableComponent?.reactToCollision(it) }
        }
    }

    private fun applyVelocityX() {
        when {
            keyboardItems[KeyEvent.VK_D] -> {
                isWalking = true
                velocityX = 3.5f
            }
            keyboardItems[KeyEvent.VK_A] -> {
                isWalking = true
                velocityX = -3.5f
            }
            else -> velocityX = 0f
        }
    }

    private fun applyVelocityY() {
        if (keyboardItems[KeyEvent.VK_S]) {
            velocityY = 5.0f
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
            val jumpTimeLimit = 600f
            if (jumpTime >= jumpTimeLimit) {
                jumpTime = 0f
                canJump = true
            }
        }
    }

    private fun processGravityEffect(deltaTime: Float) {
        val gravity = 1f
        if (jumpState && currentBottomPlatform == null) { //System.out.println("Applying gravity");
            velocityY += gravity * deltaTime / 10
        }
    }

    private fun processAnimation() = with(animationComponent) {
        this?.let {
            if (velocityX == 0f && velocityY == 0f) {
                setAnimation(animations[2])
                currentAnim.setCurrentFrameX(0)
                currentAnim.setCurrentFrameY(2)
                isWalking = false
            } else if (velocityY != 0f && velocityX != 0f) {
                // TODO wtf
            } else if (velocityX != 0f && !jumpState && currentAnim != animations[0]) {
                setWalkAnim()
            } else if (velocityX != 0f && jumpState) {
                setJumpAnimation()
            } else if (velocityY > 0f && velocityY <= 0.2f) {
                setAnimation(animations[2])
                currentAnim.setCurrentFrameX(0)
                currentAnim.setCurrentFrameY(2)
            } else {
                // do nothing
            }
        }
    }

    private fun changePosition(deltaTime: Float) = with(graphicalComponent.box) {
        this?.let {
            posY += velocityY * deltaTime / 20
            posX += velocityX * deltaTime / 20
        }
    }

    private fun updateCollisionPoints() = with(graphicalComponent.box) {
        this?.let {
            collisionPoints[0] = PointF(posX, posY)
            collisionPoints[1] = PointF(posX, bottomY)
            collisionPoints[2] = PointF(rightX, bottomY)
            collisionPoints[3] = PointF(rightX, posY)
            collisionPoints[4] = PointF(posX + width / 2, posY)
            collisionPoints[5] = PointF(posX + width / 2, bottomY)
            collisionPoints[6] = PointF(posX, posY + height / 2)
            collisionPoints[7] = PointF(rightX, posY + height / 2)
            collisionPoints[8] = PointF(posX, posY + height / 4)
            collisionPoints[9] = PointF(posX, bottomY - height / 4)
            collisionPoints[10] = PointF(rightX, posY + height / 4)
            collisionPoints[11] = PointF(rightX, bottomY - height / 5)
        }
    }

    fun preventCollision() {
        velocityY = 0.0f
        velocityX = 0.0f
        keyboardItems[KeyEvent.VK_W] = false
        keyboardItems[KeyEvent.VK_S] = false
        keyboardItems[KeyEvent.VK_A] = false
        keyboardItems[KeyEvent.VK_D] = false
        isWalking = false
    }

    private fun jump() {
        velocityY -= 20f
        setAirFloating()
    }

    private fun setAirFloating() {
        jumpState = true
        canJump = false
    }

    private fun setJumpAnimation() = animationComponent?.let {
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