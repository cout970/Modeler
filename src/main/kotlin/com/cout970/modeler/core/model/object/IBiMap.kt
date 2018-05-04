package com.cout970.modeler.core.model.`object`

import kotlinx.collections.immutable.*

interface BiMultimap<K, V> : Iterable<Pair<K, List<V>>> {
    val size: Int
    val keySize: Int

    operator fun get(key: K): Set<V>

    fun getReverse(value: V): K?

    fun hasKey(key: K): Boolean

    fun hasValue(value: V): Boolean

    operator fun set(key: K, value: V): BiMultimap<K, V>

    fun addAll(key: K, values: Iterable<V>): BiMultimap<K, V>

    fun remove(key: K): BiMultimap<K, V>

    fun set(key: K, values: Set<V>): BiMultimap<K, V>

    fun removeValue(key: K, value: V): BiMultimap<K, V>

    fun removeAll(values: Set<V>): BiMultimap<K, V>

    operator fun plus(other: BiMultimap<K, V>): BiMultimap<K, V>
}

fun <K, V> emptyBiMultimap() = ImmutableBiMultimap<K, V>(immutableMapOf(), immutableMapOf())

fun <K, V> biMultimapOf(vararg entries: Pair<K, Collection<V>>): ImmutableBiMultimap<K, V> {
    return entries.fold(emptyBiMultimap()) { acc, entry -> acc.addAll(entry.first, entry.second) }
}

class ImmutableBiMultimap<K, V>(
        val direct: ImmutableMap<K, Set<V>>,
        val reverse: ImmutableMap<V, K>
) : BiMultimap<K, V> {

    override val keySize: Int get() = direct.size
    override val size: Int get() = reverse.size

    override fun get(key: K) = direct[key] ?: emptySet()

    override fun getReverse(value: V): K? = reverse[value]

    override fun set(key: K, value: V): ImmutableBiMultimap<K, V> {
        return if (key in direct) {
            val set = direct.getValue(key)

            if (value in set) {
                this
            } else {
                val newDirect = direct + (key to (set + value))
                val newReverse = reverse + (value to key)
                ImmutableBiMultimap(newDirect, newReverse)
            }
        } else {
            val newDirect = direct + (key to (setOf(value)))
            val newReverse = reverse + (value to key)
            ImmutableBiMultimap(newDirect, newReverse)
        }
    }

    override fun set(key: K, values: Set<V>): ImmutableBiMultimap<K, V> {
        val newDirect = direct + (key to values)
        val newReverse = reverse + values.map { it to key }
        return ImmutableBiMultimap(newDirect, newReverse)
    }

    override fun addAll(key: K, values: Iterable<V>): ImmutableBiMultimap<K, V> {
        var bimap: ImmutableBiMultimap<K, V> = this
        values.forEach {
            bimap = bimap.set(key, it)
        }
        return bimap
    }

    override fun remove(key: K): ImmutableBiMultimap<K, V> {
        if (key in direct) {
            val values = direct.getValue(key)
            val newDirect = direct - key
            val newReverse = reverse - values
            return ImmutableBiMultimap(newDirect, newReverse)
        }
        return this
    }

    override fun removeValue(key: K, value: V): ImmutableBiMultimap<K, V> {
        if (key in direct) {
            val values = direct.getValue(key) - value
            val newDirect = direct + (key to values)
            val newReverse = reverse - value
            return ImmutableBiMultimap(newDirect, newReverse)
        }
        return this
    }

    override fun removeAll(values: Set<V>): BiMultimap<K, V> {
        val newDirect = direct.mapValues { it.value - values }.toImmutableMap()
        val newReverse = reverse - values
        return ImmutableBiMultimap(newDirect, newReverse)
    }

    override fun plus(other: BiMultimap<K, V>): BiMultimap<K, V> {
        return other.fold(this) { acc, (k, v) -> acc.addAll(k, v) }
    }

    override fun iterator(): Iterator<Pair<K, List<V>>> = direct.asSequence().map { it.key to it.value.toList() }.iterator()

    override fun hasKey(key: K): Boolean = key in direct

    override fun hasValue(value: V): Boolean = value in reverse

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ImmutableBiMultimap<*, *>) return false

        if (direct != other.direct) return false
        if (reverse != other.reverse) return false

        return true
    }

    override fun hashCode(): Int {
        var result = direct.hashCode()
        result = 31 * result + reverse.hashCode()
        return result
    }
}