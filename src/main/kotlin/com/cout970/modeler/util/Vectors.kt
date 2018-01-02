package com.cout970.modeler.util

import com.cout970.matrix.api.IMatrix4
import com.cout970.matrix.extensions.mat4Of
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.api.IVector4
import com.cout970.vector.extensions.*
import org.joml.*

/**
 * Created by cout970 on 2017/01/13.
 */

operator fun IVector2.component1() = x
operator fun IVector2.component2() = y

operator fun IVector3.component1() = x
operator fun IVector3.component2() = y
operator fun IVector3.component3() = z

inline fun IVector3.withX(x: Number) = vec3Of(x, y, z)
inline fun IVector3.withY(y: Number) = vec3Of(x, y, z)
inline fun IVector3.withZ(z: Number) = vec3Of(x, y, z)


fun IVector3.toVector2() = vec2Of(xd, yd)

fun IVector2.rotateAround(center: IVector2, angle: Double): IVector2 {
    val normalizedPos = this - center
    val rotatedPos = normalizedPos.rotate(angle)
    return rotatedPos + center
}

/**
 * http://stackoverflow.com/questions/3120357/get-closest-point-to-a-line
 * Port to C# made by N.Schilke using the code of Justin L.
 */
fun getClosestPointOnLineSegment(A: IVector3, B: IVector3, P: IVector3): IVector3 {
    val AP = P - A       //Vector from A to P
    val AB = B - A       //Vector from A to B

    val magnitudeAB = AB.lengthSq()          //Magnitude of AB vector (it's length squared)
    val ABAPproduct = AP dot AB              //The DOT product of a_to_p and a_to_b
    val distance = ABAPproduct / magnitudeAB //The normalized "distance" from a to your closest point

    //Check if P projection is over vectorAB
    if (distance < 0) {
        return A
    } else if (distance > 1) {
        return B
    } else {
        return A + AB * distance
    }
}

fun Number.toRads() = Math.toRadians(this.toDouble())
fun Number.toDegrees() = Math.toDegrees(this.toDouble())

inline val Vector4d.xyz get() = Vector3d(x, y, z)

fun Vector2d.toIVector(): IVector2 = vec2Of(x, y)
fun Vector3d.toIVector(): IVector3 = vec3Of(x, y, z)
fun Vector4d.toIVector(): IVector4 = vec4Of(x, y, z, w)
fun Vector2f.toIVector(): IVector2 = vec2Of(x, y)
fun Vector3f.toIVector(): IVector3 = vec3Of(x, y, z)
fun Vector4f.toIVector(): IVector4 = vec4Of(x, y, z, w)
fun Vector2i.toIVector(): IVector2 = vec2Of(x, y)
fun Vector3i.toIVector(): IVector3 = vec3Of(x, y, z)
fun Vector4i.toIVector(): IVector4 = vec4Of(x, y, z, w)
fun IVector2.toJoml2f(): Vector2f = Vector2f(xf, yf)
fun IVector2.toJoml2d(): Vector2d = Vector2d(xd, yd)
fun IVector2.toJoml2i(): Vector2i = Vector2i(xi, yi)
fun IVector3.toJoml3f(): Vector3f = Vector3f(xf, yf, zf)
fun IVector3.toJoml3d(): Vector3d = Vector3d(xd, yd, zd)
fun IVector3.toJoml3i(): Vector3i = Vector3i(xi, yi, zi)
fun IVector4.toJoml4f(): Vector4f = Vector4f(xf, yf, zf, wf)
fun IVector4.toJoml4d(): Vector4d = Vector4d(xd, yd, zd, wd)
fun IVector4.toJoml4i(): Vector4i = Vector4i(xi, yi, zi, wi)
fun Matrix4d.toIMatrix(): IMatrix4 = mat4Of(
        m00(), m01(), m02(), m03(),
        m10(), m11(), m12(), m13(),
        m20(), m21(), m22(), m23(),
        m30(), m31(), m32(), m33())

fun IMatrix4.toJOML(): Matrix4d = Matrix4d(
        m00d, m01d, m02d, m03d,
        m10d, m11d, m12d, m13d,
        m20d, m21d, m22d, m23d,
        m30d, m31d, m32d, m33d)

fun IQuaternion.toJOML(): Quaterniond {
    return Quaterniond(xd, yd, zd, wd)
}

fun IVector3.toColor() = Vector4f(xf, yf, zf, 1f)
fun Quaterniond.toIQuaternion(): IQuaternion = quatOf(x,
        y, z, w)

/**
 * Created by cout970 on 2016/12/07.
 */

fun IVector2.isInside(pos: IVector2, size: IVector2): Boolean {
    return xd > pos.xd && xd < pos.xd + size.xd &&
           yd > pos.yd && yd < pos.yd + size.yd
}

private fun IVector3.scale(center: IVector3, scale: IVector3): IVector3 {
    val pos = this - center
    val newPos = pos * scale
    return newPos + center
}

private fun IVector2.scale(center: IVector2, scale: IVector2): IVector2 {
    val pos = this - center
    val newPos = pos * scale
    return newPos + center
}

fun IVector3.scale(center: IVector3, axis: IVector3, offset: Float): IVector3 {
    val distance = this.distanceInAxis(center, axis)
    if (distance == 0.0) return this
    return scale(center, Vector3.ONE + axis * offset / distance)
}

fun IVector2.scale(center: IVector2, axis: IVector2, offset: Float): IVector2 {
    val distance = this.distanceInAxis(center, axis)
    if (distance == 0.0) return this
    return scale(center, Vector2.ONE + axis * offset / distance)
}

fun IVector2.distanceInAxis(point: IVector2, axis: IVector2): Double {
    val norm = axis.normalize()
    return Math.abs((norm dot point) - (norm dot this))
}

fun IVector3.distanceInAxis(point: IVector3, axis: IVector3): Double {
    val norm = axis.normalize()
    return Math.abs((norm dot point) - (norm dot this))
}

inline fun quatOfAngles(x: Number, y: Number, z: Number): IQuaternion {
    return quatOfAngles(vec3Of(x, y, z))
}

// uses degrees
fun quatOfAngles(angles: IVector3): IQuaternion {
    val rads = angles.toRadians()
    return Quaterniond().rotateXYZ(rads.x.toDouble(), rads.y.toDouble(), rads.z.toDouble()).toIQuaternion()
}

inline fun quatOfAxisAngled(x: Number, y: Number, z: Number, angle: Number): IQuaternion {
    return Quaterniond().rotateAxis(angle.toDouble(), x.toDouble(), y.toDouble(), z.toDouble()).toIQuaternion()
}

// degrees
fun quatOfAxisAngled(angles: IVector3, angle: Number): IQuaternion {

    return Quaterniond().rotateAxis(
            angle.toDouble(),
            angles.x.toRads(),
            angles.y.toRads(),
            angles.z.toRads()
    ).toIQuaternion()
}

fun IQuaternion.transform(pos: IVector3): IVector3 {
    return toJOML().transform(pos.toJoml3d()).toIVector()
}

fun IVector3.rotateAround(pivot: IVector3, rotation: IQuaternion): IVector3 {
    var dir: IVector3 = this - pivot // get point direction relative to pivot
    dir = rotation.transform(dir) // rotate it
    return dir + pivot // calculate final point
}

fun IVector2.hasNaN() = xd.isNaN() || yd.isNaN()
fun IVector3.hasNaN() = xd.isNaN() || yd.isNaN() || zd.isNaN()
fun IVector4.hasNaN() = xd.isNaN() || yd.isNaN() || zd.isNaN() || wd.isNaN()

fun IQuaternion.invert() = toJOML().invert().toIQuaternion()

// http://stackoverflow.com/questions/19649452/given-a-single-arbitrary-unit-vector-what-is-the-best-method-to-compute-an-arbi
fun IVector3.getPerpendicularPlane(): Pair<IVector3, IVector3> {
    val axis = getDominantAxis()
    val aux = when (axis) {
        0 -> vec3Of(-yd - zd, xd, xd)
        1 -> vec3Of(yd, -xd - zd, yd)
        else -> vec3Of(zd, zd, -xd - yd)
    }
    val a = (this cross aux).normalize()
    val b = (a cross this).normalize()

    return a to b
}

// 0 -> X
// 1 -> Y
// 2 -> Z
fun IVector3.getDominantAxis(): Int {
    val x = Math.abs(xf)
    val y = Math.abs(yf)
    val z = Math.abs(zf)

    return if (x > y) {
        if (x > z) 0 else 2
    } else {
        if (y > z) 1 else 2
    }
}

fun Boolean.toInt() = if (this) 1 else 0

infix fun IVector3.rotationTo(other: IVector3): IQuaternion {
    val q = Quaterniond().rotationTo(this.toJoml3d(), other.toJoml3d())
    return q.toIQuaternion()
}

fun IQuaternion.toAxisRotations(): IVector3 {
    return toJOML().getEulerAnglesXYZ(Vector3d()).toIVector().toDegrees()
}

fun IVector3.fromAxisRotations(): IQuaternion {
    return Quaterniond().rotateYXZ(xd.toRads(), yd.toRads(), zd.toRads()).toIQuaternion()
}

fun IQuaternion.normalize(): IQuaternion {
    return toJOML().normalize().toIQuaternion()
}

fun Pair<IVector3, IQuaternion>.fromPivotToOrigin(): Pair<IVector3, IQuaternion> {
    val finalPos = -(second.toJOML().transform((-first).toJoml3d()).toIVector()) + first
    return finalPos to second
}