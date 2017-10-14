package com.cout970.modeler.util

import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.raytrace.Ray
import com.cout970.raytrace.RayTraceResult
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
import org.funktionale.option.Option
import org.lwjgl.PointerBuffer
import org.lwjgl.system.MemoryUtil
import java.awt.Color

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

fun <T> List<T>.combine(multi: Boolean, element: T): List<T> {
    if (multi) {
        if (element in this) {
            return this - element
        } else {
            return this + element
        }
    } else {
        if (this.size == 1 && element in this) {
            return emptyList()
        } else {
            return listOf(element)
        }
    }
}

inline fun <T, R> Option<T>.ifDefined(func: (T) -> R): Option<R> {
    if (isDefined()) {
        return map(func)
    }
    return Option.None
}

fun <T> List<T>.getCyclic(index: Int): T {
    val ind = index % size
    return get(if (ind < 0) ind + size else ind)
}

fun getColor(hash: Int): IVector3 {
    val c = Color.getHSBColor((hash.toDouble() / Int.MAX_VALUE.toDouble()).toFloat() * 360f, 0.5f, 1f)
    return vec3Of(c.blue / 255f, c.green / 255f, c.red / 255f)
}