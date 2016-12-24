package com.cout970.modeler.util

/**
 * Created by cout970 on 2016/11/29.
 */
class Cache<K, T>(val limit: Int = 5) {

    private val entries = mutableMapOf<K, Entry<T>>()
    private var count = 0
    var onRemove: ((K, T) -> Unit)? = null

    fun getOrCompute(key: K, func: (K) -> T): T {
        if (key in entries) {
            val entry = entries[key]!!
            entry.count = count++
            return entry.value
        } else {
            if (entries.size >= limit) {
                val oldest = entries.minBy { it.value.count }!!
                entries.remove(oldest.key)
                onRemove?.invoke(oldest.key, oldest.value.value)
            }
            val value = func(key)
            entries.put(key, Entry(value, count++))
            return value
        }
    }

    override fun toString(): String {
        return "Cache(limit=$limit, entries=$entries, count=$count)"
    }

    class Entry<out T>(val value: T, var count: Int)

    fun clear() {
        onRemove?.let {
            for ((key, value) in entries) {
                it.invoke(key, value.value)
            }
        }
        entries.clear()
        count = 0
    }
}