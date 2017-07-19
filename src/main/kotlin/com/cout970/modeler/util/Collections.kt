package com.cout970.modeler.util

import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.raytrace.Ray
import com.cout970.raytrace.RayTraceResult
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.distance
import com.cout970.vector.extensions.div
import com.cout970.vector.extensions.plus
import org.lwjgl.PointerBuffer
import org.lwjgl.system.MemoryUtil

/**
 * Created by cout970 on 2016/12/09.
 */

inline fun <T> Iterable<T>.replace(predicate: (T) -> Boolean, transform: (T) -> T): List<T> {
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

fun List<RayTraceResult>.getClosest(ray: Ray): RayTraceResult? {
    return when {
        isEmpty() -> null
        size == 1 -> first()
        else -> {
            sortedBy { it.hit.distance(ray.start) }.first()
        }
    }
}

infix fun <A, B> List<A>.join(other: List<B>): List<Pair<A, B>> {
    require(size == other.size) { "Invalid list sizes: this.size = $size, other.size = ${other.size}" }
    return this.mapIndexed { index, element ->
        element to other[index]
    }
}

infix fun IntArray.join(other: IntArray): List<Pair<Int, Int>> {
    require(size == other.size) { "Invalid array sizes: this.size = $size, other.size = ${other.size}" }
    return this.mapIndexed { index, element ->
        element to other[index]
    }
}

fun IMesh.middle(): IVector3 = pos.middle()

fun List<IVector3>.middle(): IVector3 {
    var acum = Vector3.ORIGIN
    forEach {
        acum += it
    }
    return acum / size
}

fun List<String>.toPointerBuffer(): PointerBuffer {
    val pointer = MemoryUtil.memAllocPointer(size)
    forEach { pointer.put(MemoryUtil.memUTF8(it)) }
    pointer.flip()
    return pointer
}