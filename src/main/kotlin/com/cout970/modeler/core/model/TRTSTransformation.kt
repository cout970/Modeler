package com.cout970.modeler.core.model

import com.cout970.matrix.api.IMatrix4
import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.util.*
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.interpolate
import com.cout970.vector.extensions.plus
import com.cout970.vector.extensions.times
import org.joml.Matrix4d

/**
 * Created by cout970 on 2017/05/14.
 */
data class TRTSTransformation(
        val translation: IVector3 = Vector3.ORIGIN,
        val rotation: IVector3 = Vector3.ORIGIN,
        val pivot: IVector3 = Vector3.ORIGIN,
        val scale: IVector3 = Vector3.ONE
) : ITransformation {

    companion object {
        val IDENTITY = TRTSTransformation(Vector3.ORIGIN, Vector3.ORIGIN, Vector3.ORIGIN, Vector3.ONE)
    }

    val quatRotation: IQuaternion get() = quatOfAngles(rotation)

    // Gson pls
    private constructor() : this(Vector3.ORIGIN, Vector3.ORIGIN, Vector3.ORIGIN, Vector3.ONE)

    override val matrix: IMatrix4 by lazy {
        Matrix4d().apply {
            translate(translation.xd, translation.yd, translation.zd)

            translate(pivot.xd, pivot.yd, pivot.zd)
            rotate(quatOfAngles(rotation).toJOML())
            translate(-pivot.xd, -pivot.yd, -pivot.zd)

            scale(scale.xd, scale.yd, scale.zd)
        }.toIMatrix()
    }

    fun merge(other: TRTSTransformation): TRTSTransformation {
        return TRTSTransformation(
                translation = quatOfAngles(other.rotation).transform(this.translation) + other.translation,
                rotation = (quatOfAngles(other.rotation) * quatOfAngles(this.rotation)).toAxisRotations(),
                pivot = other.pivot + this.pivot,
                scale = this.scale * other.scale
        )
    }

    fun lerp(other: TRTSTransformation, step: Float): TRTSTransformation {
        return TRTSTransformation(
                translation = this.translation.interpolate(other.translation, step.toDouble()),
                rotation = quatOfAngles(this.rotation).lerp(quatOfAngles(other.rotation), step.toDouble()).toAxisRotations(),
                pivot = this.pivot.interpolate(other.pivot, step.toDouble()),
                scale = this.scale.interpolate(other.scale, step.toDouble())
        )
    }

    fun toTRS(): TRSTransformation {
        val base = TRSTransformation.fromRotationPivot(pivot, rotation)

        return TRSTransformation(
                translation = translation + base.translation,
                rotation = base.rotation,
                scale = scale
        )
    }

    override fun plus(other: ITransformation): ITransformation {
        return when (other) {
            is TRSTransformation -> this.merge(other.toTRTS())
            is TRTSTransformation -> this.merge(other)
            else -> error("Unknown ITransformation type: $other, ${other::class.java.name}")
        }
    }
}