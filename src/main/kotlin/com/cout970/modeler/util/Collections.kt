package com.cout970.modeler.util

import com.cout970.raytrace.Ray
import com.cout970.raytrace.RayTraceResult
import com.cout970.vector.extensions.distance

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

inline fun <T, R> Iterable<T>.flatMapIndexed(transform: (index: Int, T) -> Iterable<R>): List<R> {
    val destination = mutableListOf<R>()
    var index = 0
    for (element in this) {
        val list = transform(index, element)
        destination.addAll(list)
        index++
    }
    return destination
}

inline fun <T> Iterable<T>.filterNotIndexed(predicate: (index: Int, T) -> Boolean): List<T> {
    val destination = mutableListOf<T>()
    var index = 0
    for (element in this) {
        if (!predicate(index, element)) {
            destination.add(element)
        }
        index++
    }
    return destination
}


inline fun <reified F> Iterable<*>.castTo(): List<F> = map { it as F }

fun <T> List<Pair<RayTraceResult, T>>.getClosest(ray: Ray): Pair<RayTraceResult, T>? {
    return when {
        isEmpty() -> null
        size == 1 -> first()
        else -> {
            sortedBy { it.first.hit.distance(ray.start) }.first()
        }
    }
}
