package com.cout970.modeler.core.model

import com.cout970.matrix.api.IMatrix4
import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.util.quatOfAngles
import com.cout970.modeler.util.toIMatrix
import com.cout970.modeler.util.toJOML
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

    val position: IVector3 = preRotation + postRotation

    // Gson pls
    private constructor() : this(Vector3.ORIGIN, Quaternion.IDENTITY, Vector3.ORIGIN, Vector3.ONE)

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

//    fun merge(other: TRTSTransformation): TRTSTransformation {
//      TODO
//    }

    /*
    public void RotateAround(Vector3 point, Vector3 axis, float angle)
		{
			Vector3 vector = this.position;
			Quaternion rotation = Quaternion.AngleAxis(angle, axis);
			Vector3 vector2 = vector - point;
			vector2 = rotation * vector2;
			vector = point + vector2;
			this.position = vector;
			this.RotateAroundInternal(axis, angle * 0.0174532924f);
		}
	*/
}