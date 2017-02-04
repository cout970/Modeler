package com.cout970.modeler.util

import com.cout970.matrix.api.IMatrix4
import com.cout970.matrix.extensions.*
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
operator fun IVector3.component3() = z

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