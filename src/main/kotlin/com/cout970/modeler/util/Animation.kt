package com.cout970.modeler.util

import kotlin.math.absoluteValue
import kotlin.math.ceil
import kotlin.math.roundToInt

fun Float.toFrame(): Int = (this * 60f).roundToInt()

fun Int.fromFrame(): Float = this / 60f

fun Double.roundTo(value: Double): Double = ceil(this * value) / value

inline fun Int.isOdd(): Boolean = this % 2 != 0
inline fun Int.isEven(): Boolean = this % 2 == 0

fun Double.aprox(other: Double, epsilon: Double = 0.001) = (this - other).absoluteValue < epsilon