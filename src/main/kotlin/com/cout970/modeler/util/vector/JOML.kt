package com.cout970.modeler.util.vector

import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.api.IVector4
import org.joml.Quaterniond
import org.joml.Vector2d
import org.joml.Vector3d
import org.joml.Vector4d

/**
 * Created by cout970 on 2017/01/13.
 */

class Vector3(
        override val x: Double,
        override val y: Double,
        override val z: Double
) : Vector3d(x, y, z), IVector3

class Vector2(
        override val x: Double,
        override val y: Double
) : Vector2d(x, y), IVector2

class Vector4(
        override val x: Double,
        override val y: Double,
        override val z: Double,
        override val w: Double
) : Vector4d(x, y, z, w), IVector4

class Quaternion(
        override val x: Double,
        override val y: Double,
        override val z: Double,
        override val w: Double
) : Quaterniond(x, y, z, w), IQuaternion