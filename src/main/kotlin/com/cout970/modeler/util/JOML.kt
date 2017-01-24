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
 * Created by cout970 on 2016/12/06.
 */

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

fun Quaterniond.toIQuaternion(): IQuaternion = quatOf(x, y, z, w)