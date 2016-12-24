package com.cout970.modeler.util

/**
 * Created by cout970 on 2016/12/09.
 */

fun <T> Iterable<T>.replace(predicate: (T) -> Boolean, transform: (T) -> T): List<T> {
    val list = mutableListOf<T>()
    for (i in this) {
        list += if (predicate(i)) transform(i) else i
    }
    return list
}

fun <T> Iterable<T>.replaceWithIndex(predicate: (Int, T) -> Boolean, transform: (Int, T) -> T): List<T> {
    val list = mutableListOf<T>()
    for ((i, value) in this.withIndex()) {
        list += if (predicate(i, value)) transform(i, value) else value
    }
    return list
}