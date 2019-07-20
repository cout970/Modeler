package com.cout970.modeler.util

import kotlin.math.absoluteValue
import kotlin.math.roundToInt

fun Float.toFrame(): Int = (this * 60f).roundToInt()

fun Int.fromFrame(): Float = this / 60f

fun Double.aprox(other: Double, epsilon: Double = 0.001) = (this - other).absoluteValue < epsilon