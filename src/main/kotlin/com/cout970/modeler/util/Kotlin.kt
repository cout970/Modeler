package com.cout970.modeler.util

/**
 * Created by cout970 on 2017/06/14.
 */


class Temp<out T>(val eval: Boolean, val ifTrue: () -> T)

infix fun <T> Boolean.ifTrue(func: () -> T) = Pair(this, func)

inline infix fun <T> Pair<Boolean, () -> T>.ifFalse(func: () -> T): T {
    return if (first) second() else func()
}