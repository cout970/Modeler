package com.cout970.modeler.core.model

import com.cout970.matrix.api.IMatrix4
import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.util.*
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
import org.joml.Matrix4d

/**
 * Created by cout970 on 2017/07/21.
 */
data class TRTSTransformation(
        val preRotation: IVector3 = Vector3.ORIGIN,
        val rotation: IVector3 = Vector3.ORIGIN, //in degrees
        val postRotation: IVector3 = Vector3.ORIGIN,
        val scale: IVector3 = Vector3.ONE
) : ITransformation {

    companion object {
        val IDENTITY = TRTSTransformation(Vector3.ORIGIN, Quaternion.IDENTITY, Vector3.ORIGIN, Vector3.ONE)

        fun fromRotationPivot(pivot: IVector3, rotation: IVector3): TRTSTransformation {
            return TRTSTransformation(pivot, rotation, -pivot, Vector3.ONE)
        }
    }

    val translation: IVector3 get() = preRotation + postRotation
    val rotationPoint: IVector3 get() = preRotation
    val quaternion: IQuaternion get() = quatOfAngles(rotation)

    fun toOriginRotation() = (rotationPoint to quaternion).fromPivotToOrigin()

    // Gson pls
    private constructor() : this(Vector3.ORIGIN, Quaternion.IDENTITY, Vector3.ORIGIN, Vector3.ONE)

    constructor(pre: IVector3, rot: IQuaternion, post: IVector3, scale: IVector3)
            : this(pre, rot.toAxisRotations(), post, scale)

    override val matrix: IMatrix4 by lazy {
        Matrix4d().apply {
            translate(preRotation.xd, preRotation.yd, preRotation.zd)
            rotate(quatOfAngles(rotation).toJOML())
            translate(postRotation.xd, postRotation.yd, postRotation.zd)
            scale(scale.xd, scale.yd, scale.zd)
        }.toIMatrix()
    }

    fun translate(translation: IVector3): TRTSTransformation {
        return copy(
                preRotation = preRotation + translation,
                rotation = rotation,
                postRotation = postRotation + translation,
                scale = scale
        )
    }

    // TODO fix this
    fun merge(other: TRTSTransformation): TRTSTransformation {
        val thisTrans = this.translation
        val thisPivotRot = this.toOriginRotation()

        val otherTrans = other.translation
        val otherPivotRot = other.toOriginRotation()

        val finalTrans = thisTrans + otherTrans
        val finalPivotTrans = thisPivotRot.first + otherPivotRot.first
        val finalPivotRot = (thisPivotRot.second * otherPivotRot.second).toAxisRotations()
        val finalScale = this.scale * other.scale

        return TRTSTransformation(
                preRotation = finalTrans + finalPivotTrans,
                rotation = finalPivotRot,
                postRotation = Vector3.ORIGIN,
                scale = finalScale
        )
    }
}