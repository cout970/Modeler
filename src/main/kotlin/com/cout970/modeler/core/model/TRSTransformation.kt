package com.cout970.modeler.core.model

import com.cout970.matrix.api.IMatrix4
import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.util.*
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
import org.joml.Matrix4d
import org.joml.Quaterniond
import org.joml.Vector3d
import kotlin.math.PI
import kotlin.math.cos

/**
 * Created by cout970 on 2017/05/14.
 */
data class TRSTransformation(
    val translation: IVector3 = Vector3.ORIGIN,
    val rotation: IQuaternion = Quaternion.IDENTITY,
    val scale: IVector3 = Vector3.ONE
) : ITransformation {

    companion object {
        val IDENTITY = TRSTransformation(Vector3.ORIGIN, Quaternion.IDENTITY, Vector3.ONE)

        @Suppress("UnnecessaryVariable")
        fun fromRotationPivot(pivot: IVector3, rotation: IQuaternion): TRSTransformation {
            val preRotation = pivot
            val postRotation = -pivot

            val matrix = Matrix4d().apply {
                translate(preRotation.xd, preRotation.yd, preRotation.zd)
                rotate(rotation.toJOML())
                translate(postRotation.xd, postRotation.yd, postRotation.zd)
            }

            val pos = vec3Of(matrix.m30(), matrix.m31(), matrix.m32())
            val rot = Quaterniond().setFromUnnormalized(matrix).toIQuaternion()

            return TRSTransformation(pos, rot, Vector3.ONE)
        }

        fun fromRotationPivot(pivot: IVector3, rotation: IVector3): TRSTransformation {
            return fromRotationPivot(pivot, quatOfAngles(rotation))
        }

        fun fromScalePivot(point: IVector3, scale: IVector3): TRSTransformation {
            return TRSTransformation(translation = point - point * scale, scale = scale)
        }

        fun fromMatrix(mat: IMatrix4): TRSTransformation {
            val joml = mat.toJOML()
            val translation = joml.getTranslation(Vector3d()).toIVector()
            val rotation = Quaterniond().setFromUnnormalized(joml).toIQuaternion()
//            val rotation = joml.getUnnormalizedRotation(Quaterniond()).normalize().toIQuaternion()
            val scale = joml.getScale(Vector3d()).toIVector()

            return TRSTransformation(translation, rotation, scale)
        }
    }

    // Gson pls
    private constructor() : this(Vector3.ORIGIN, Quaternion.IDENTITY, Vector3.ONE)

    val euler: EulerRotation get() = rotation.toEuler()

    override val matrix: IMatrix4 by lazy {
        Matrix4d().apply {
            translate(translation.xd, translation.yd, translation.zd)
            rotate(rotation.toJOML())
            scale(scale.xd, scale.yd, scale.zd)
        }.toIMatrix()
    }

    fun merge(other: TRSTransformation): TRSTransformation {
        return TRSTransformation(
            translation = other.rotation.transform(this.translation) * other.scale + other.translation,
            rotation = other.rotation * this.rotation,
            scale = other.scale * this.scale
        )
    }

    operator fun times(other: TRSTransformation): TRSTransformation {
        return TRSTransformation(
            translation = this.rotation.transform(other.translation) + this.translation * other.scale,
            rotation = this.rotation * other.rotation,
            scale = this.scale * other.scale
        )
    }

    fun lerp(other: TRSTransformation, step: Float): TRSTransformation {
        return TRSTransformation(
            translation = this.translation.interpolate(other.translation, step.toDouble()),
            rotation = this.rotation.lerp(other.rotation, step.toDouble()),
            scale = this.scale.interpolate(other.scale, step.toDouble())
        )
    }

    fun cosineInterpolate(other: TRSTransformation, step: Float): TRSTransformation {
        fun cosine(y1: Double, y2: Double, mu: Float): Double {
            val mu2 = (1 - cos(mu * PI)) / 2
            return y1 * (1 - mu2) + y2 * mu2
        }

        val translation = vec3Of(
            cosine(this.translation.xd, other.translation.xd, step),
            cosine(this.translation.yd, other.translation.yd, step),
            cosine(this.translation.zd, other.translation.zd, step)
        )
        val scale = vec3Of(
            cosine(this.scale.xd, other.scale.xd, step),
            cosine(this.scale.yd, other.scale.yd, step),
            cosine(this.scale.zd, other.scale.zd, step)
        )

        return TRSTransformation(
            translation = translation,
            rotation = this.rotation.slerp(other.rotation, step.toDouble()),
            scale = scale
        )
    }

    fun toTRTS(): TRTSTransformation {
        return TRTSTransformation(
            translation = translation,
            rotation = rotation.toAxisRotations(),
            pivot = Vector3.ZERO,
            scale = scale
        )
    }

    override fun plus(other: ITransformation): ITransformation {
        return when (other) {
            is TRSTransformation -> this.merge(other)
            is TRTSTransformation -> this.merge(other.toTRS())
            else -> error("Unknown ITransformation type: $other, ${other::class.java.name}")
        }
    }
}

fun ITransformation.toTRS() = when (this) {
    is TRSTransformation -> this
    is TRTSTransformation -> this.toTRS()
    else -> error("Type: ${javaClass.name}")
}

fun ITransformation.toTRTS() = when (this) {
    is TRSTransformation -> this.toTRTS()
    is TRTSTransformation -> this
    else -> error("Type: ${javaClass.name}")
}