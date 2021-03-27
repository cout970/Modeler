package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.core.model.TRTSTransformation
import com.cout970.modeler.util.EulerRotation
import com.cout970.vector.extensions.vec3Of
import javax.script.ScriptEngineManager

val scriptEngine = ScriptEngineManager(null).getEngineByName("JavaScript")!!

fun updateTransformation(value: ITransformation, cmd: String, input: String, offset: Float): ITransformation? {
    val newValue: ITransformation

    when (value) {
        is TRSTransformation -> newValue = when (cmd) {
            "size.x" -> setTRSSizeX(value, x = getValue(input, value.scale.xf) + offset)
            "size.y" -> setTRSSizeY(value, y = getValue(input, value.scale.yf) + offset)
            "size.z" -> setTRSSizeZ(value, z = getValue(input, value.scale.zf) + offset)

            "pos.x" -> setTRSPosX(value, x = getValue(input, value.translation.xf) + offset)
            "pos.y" -> setTRSPosY(value, y = getValue(input, value.translation.yf) + offset)
            "pos.z" -> setTRSPosZ(value, z = getValue(input, value.translation.zf) + offset)

            "rot.x" -> setTRSRotationX(value, x = getValue(input, value.euler.angles.xf) + offset * 15f)
            "rot.y" -> setTRSRotationY(value, getValue(input, value.euler.angles.yf) + offset * 15f)
            "rot.z" -> setTRSRotationZ(value, z = getValue(input, value.euler.angles.zf) + offset * 15f)

            else -> value
        }
        is TRTSTransformation -> newValue = when (cmd) {
            "size.x" -> setTRTSSizeX(value, x = getValue(input, value.scale.xf) + offset)
            "size.y" -> setTRTSSizeY(value, y = getValue(input, value.scale.yf) + offset)
            "size.z" -> setTRTSSizeZ(value, z = getValue(input, value.scale.zf) + offset)

            "pos.x" -> setTRTSPosX(value, x = getValue(input, value.translation.xf) + offset)
            "pos.y" -> setTRTSPosY(value, y = getValue(input, value.translation.yf) + offset)
            "pos.z" -> setTRTSPosZ(value, z = getValue(input, value.translation.zf) + offset)

            "rot.x" -> setTRTSRotationX(value, x = getValue(input, value.rotation.xf) + offset * 15f)
            "rot.y" -> setTRTSRotationY(value, y = getValue(input, value.rotation.yf) + offset * 15f)
            "rot.z" -> setTRTSRotationZ(value, z = getValue(input, value.rotation.zf) + offset * 15f)

            "pivot.x" -> setTRTSPivotX(value, x = getValue(input, value.translation.xf) + offset)
            "pivot.y" -> setTRTSPivotY(value, y = getValue(input, value.translation.yf) + offset)
            "pivot.z" -> setTRTSPivotZ(value, z = getValue(input, value.translation.zf) + offset)

            else -> value
        }
        else -> error("Invalid ITransformation: $value")
    }

    if (value == newValue) return null

    return newValue
}

// TRS
private fun setTRSSizeX(trans: TRSTransformation, x: Float): TRSTransformation =
        trans.copy(scale = vec3Of(x, trans.scale.y, trans.scale.z))

private fun setTRSSizeY(trans: TRSTransformation, y: Float): TRSTransformation =
        trans.copy(scale = vec3Of(trans.scale.x, y, trans.scale.z))

private fun setTRSSizeZ(trans: TRSTransformation, z: Float): TRSTransformation =
        trans.copy(scale = vec3Of(trans.scale.x, trans.scale.y, z))

// TRTS
private fun setTRTSSizeX(trans: TRTSTransformation, x: Float): TRTSTransformation =
        trans.copy(scale = vec3Of(x, trans.scale.y, trans.scale.z))

private fun setTRTSSizeY(trans: TRTSTransformation, y: Float): TRTSTransformation =
        trans.copy(scale = vec3Of(trans.scale.x, y, trans.scale.z))

private fun setTRTSSizeZ(trans: TRTSTransformation, z: Float): TRTSTransformation =
        trans.copy(scale = vec3Of(trans.scale.x, trans.scale.y, z))

// TRS
private fun setTRSPosX(trans: TRSTransformation, x: Float): TRSTransformation =
        trans.copy(translation = vec3Of(x, trans.translation.y, trans.translation.z))

private fun setTRSPosY(trans: TRSTransformation, y: Float): TRSTransformation =
        trans.copy(translation = vec3Of(trans.translation.x, y, trans.translation.z))

private fun setTRSPosZ(trans: TRSTransformation, z: Float): TRSTransformation =
        trans.copy(translation = vec3Of(trans.translation.x, trans.translation.y, z))

// TRTS
private fun setTRTSPosX(trans: TRTSTransformation, x: Float): TRTSTransformation =
        trans.copy(translation = vec3Of(x, trans.translation.y, trans.translation.z))

private fun setTRTSPosY(trans: TRTSTransformation, y: Float): TRTSTransformation =
        trans.copy(translation = vec3Of(trans.translation.x, y, trans.translation.z))

private fun setTRTSPosZ(trans: TRTSTransformation, z: Float): TRTSTransformation =
        trans.copy(translation = vec3Of(trans.translation.x, trans.translation.y, z))

// TRS
private fun setTRSRotationX(trans: TRSTransformation, x: Float): TRSTransformation {
    val oldRot = trans.euler.angles
    return trans.copy(rotation = EulerRotation(vec3Of(x.clampRot(), oldRot.yd, oldRot.zd)))
}

private fun setTRSRotationY(trans: TRSTransformation, y: Float): TRSTransformation {
    val oldRot = trans.euler.angles
    return trans.copy(rotation = EulerRotation(vec3Of(oldRot.xd, y.clampRot(), oldRot.zd)))
}

private fun setTRSRotationZ(trans: TRSTransformation, z: Float): TRSTransformation {
    val oldRot = trans.euler.angles
    return trans.copy(rotation = EulerRotation(vec3Of(oldRot.xd, oldRot.yd, z.clampRot())))
}

// TRTS
private fun setTRTSRotationX(trans: TRTSTransformation, x: Float): TRTSTransformation =
        trans.copy(rotation = vec3Of(x.clampRot(), trans.rotation.y, trans.rotation.z))

private fun setTRTSRotationY(trans: TRTSTransformation, y: Float): TRTSTransformation =
        trans.copy(rotation = vec3Of(trans.rotation.x, y.clampRot(), trans.rotation.z))

private fun setTRTSRotationZ(trans: TRTSTransformation, z: Float): TRTSTransformation =
        trans.copy(rotation = vec3Of(trans.rotation.x, trans.rotation.y, z.clampRot()))

// TRTS
private fun setTRTSPivotX(trans: TRTSTransformation, x: Float): TRTSTransformation =
        trans.copy(pivot = vec3Of(x, trans.pivot.y, trans.pivot.z))

private fun setTRTSPivotY(trans: TRTSTransformation, y: Float): TRTSTransformation =
        trans.copy(pivot = vec3Of(trans.pivot.x, y, trans.pivot.z))

private fun setTRTSPivotZ(trans: TRTSTransformation, z: Float): TRTSTransformation =
        trans.copy(pivot = vec3Of(trans.pivot.x, trans.pivot.y, z))

private fun Float.clampRot(): Double {
    return when {
        this > 180f -> this - 360f
        this < -180f -> this + 360f
        else -> this
    }.toDouble()
}

fun getValue(input: String, default: Float): Float {
    return try {
        (scriptEngine.eval(input) as? Number)?.toFloat() ?: default
    } catch (e: Exception) {
        default
    }
}