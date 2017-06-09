package com.cout970.modeler.util

/**
 * Created by cout970 on 2017/06/09.
 */

inline fun <reified T : Enum<T>> T.next() = enumValues<T>()[(ordinal + 1) % enumValues<T>().size]