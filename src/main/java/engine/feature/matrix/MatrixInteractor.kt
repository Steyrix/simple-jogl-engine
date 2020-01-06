package engine.feature.matrix

import com.hackoeur.jglm.Mat4
import com.hackoeur.jglm.Matrices
import com.hackoeur.jglm.Vec3

object MatrixInteractor {

    fun getFinalMatrix(posX: Float, posY: Float, xSize: Float, ySize: Float, rotationAngle: Float): Mat4 {
        var model = Mat4.MAT4_IDENTITY
        val rotation = Matrices.rotate(rotationAngle, Vec3(0.0f, 0.0f, 1.0f))
        val scale = getScaleMatrix(xSize, ySize)

        model = model.translate(Vec3(posX, posY, 0.0f))

        applyRotation(xSize, ySize, rotation, model)

        model = model.multiply(scale)

        return model
    }

    fun getScaleMatrix(xSize: Float, ySize: Float): Mat4 =
            Mat4(xSize, 0.0f, 0.0f, 0.0f,
                    0.0f, ySize, 0.0f, 0.0f,
                    0.0f, 0.0f, 1.0f, 0.0f,
                    0.0f, 0.0f, 0.0f, 1.0f)

    fun applyRotation(xSize: Float,
                      ySize: Float,
                      rotation: Mat4,
                      model: Mat4) {
        var outputModel = model
        outputModel = outputModel.translate(Vec3(0.5f * xSize, 0.5f * ySize, 0.0f))
        outputModel = outputModel.multiply(rotation)
        outputModel = outputModel.translate(Vec3(-0.5f * xSize, -0.5f * ySize, 0.0f))
    }
}