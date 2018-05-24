package com.cout970.modeler.core.model.`object`

import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.immutableHashMapOf
import kotlinx.collections.immutable.toImmutableMap

interface Multimap<K, V> : Iterable<Pair<K, List<V>>> {
    val size: Int
    val keySize: Int

    operator fun get(key: K): List<V>

    fun hasKey(key: K): Boolean

    fun hasValue(value: V): Boolean

    operator fun set(key: K, value: V): Multimap<K, V>

    fun addAll(key: K, values: Iterable<V>): Multimap<K, V>

    fun remove(key: K): Multimap<K, V>

    fun set(key: K, values: List<V>): Multimap<K, V>

    fun removeValue(key: K, value: V): Multimap<K, V>

    fun removeAll(values: List<V>): Multimap<K, V>

    operator fun plus(other: Multimap<K, V>): Multimap<K, V>
}

fun <K, V> emptyMultimap() = ImmutableMultimap<K, V>(immutableHashMapOf())

fun <K, V> multimapOf(vararg entries: Pair<K, Collection<V>>): ImmutableMultimap<K, V> {
    return entries.fold(emptyMultimap()) { acc, entry -> acc.addAll(entry.first, entry.second) }
}

class ImmutableMultimap<K, V>(
        val direct: ImmutableMap<K, List<V>>
) : Multimap<K, V> {

    override val keySize: Int get() = direct.size
    override val size: Int get() = direct.values.map { it.count() }.sum()

    override fun get(key: K) = direct[key] ?: emptyList()

    override fun set(key: K, value: V): ImmutableMultimap<K, V> {
        return if (key in direct) {
            val set = direct.getValue(key)

            if (value in set) {
                this
            } else {
                val newDirect = direct.put(key, set + value)
                ImmutableMultimap(newDirect)
            }
        } else {
            val newDirect = direct.put(key, listOf(value))
            ImmutableMultimap(newDirect)
        }
    }

    override fun set(key: K, values: List<V>): ImmutableMultimap<K, V> {
        val newDirect = direct.put(key, values)
        return ImmutableMultimap(newDirect)
    }

    override fun addAll(key: K, values: Iterable<V>): ImmutableMultimap<K, V> {
        var bimap: ImmutableMultimap<K, V> = this
        values.forEach {
            bimap = bimap.set(key, it)
        }
        return bimap
    }

    override fun remove(key: K): ImmutableMultimap<K, V> {
        if (key in direct) {
            val newDirect = direct.remove(key)
            return ImmutableMultimap(newDirect)
        }
        return this
    }

    override fun removeValue(key: K, value: V): ImmutableMultimap<K, V> {
        if (key in direct) {
            val values = direct.getValue(key) - value
            val newDirect = direct.put(key, values)
            return ImmutableMultimap(newDirect)
        }
        return this
    }

    override fun removeAll(values: List<V>): Multimap<K, V> {
        val newDirect = direct.mapValues { it.value - values }.toImmutableMap()
        return ImmutableMultimap(newDirect)
    }

    override fun plus(other: Multimap<K, V>): Multimap<K, V> {
        return other.fold(this) { acc, (k, v) -> acc.addAll(k, v) }
    }

    override fun iterator(): Iterator<Pair<K, List<V>>> = direct.asSequence().map { it.key to it.value.toList() }.iterator()

    override fun hasKey(key: K): Boolean = key in direct

    override fun hasValue(value: V): Boolean = direct.values.any { value in it }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ImmutableMultimap<*, *>) return false

        if (direct != other.direct) return false

        return true
    }

    override fun hashCode(): Int {
        return direct.hashCode()
    }

    override fun toString(): String {
        return "ImmutableMultimap($direct)"
    }
}