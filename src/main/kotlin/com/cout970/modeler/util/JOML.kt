package com.cout970.modeler.util

import com.cout970.matrix.api.IMatrix4
import com.cout970.matrix.extensions.mat4Of
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.api.IVector4
import com.cout970.vector.extensions.*
import org.joml.*

/**
 * Created by cout970 on 2016/12/06.
 */

fun Vector2d.toImmutable(): IVector2 = vec2Of(x, y)

fun Vector3d.toImmutable(): IVector3 = vec3Of(x, y, z)
fun Vector4d.toImmutable(): IVector4 = vec4Of(x, y, z, w)

fun Vector2f.toImmutable(): IVector2 = vec2Of(x, y)
fun Vector3f.toImmutable(): IVector3 = vec3Of(x, y, z)
fun Vector4f.toImmutable(): IVector4 = vec4Of(x, y, z, w)

fun Vector2i.toImmutable(): IVector2 = vec2Of(x, y)
fun Vector3i.toImmutable(): IVector3 = vec3Of(x, y, z)
fun Vector4i.toImmutable(): IVector4 = vec4Of(x, y, z, w)

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