package com.cout970.modeler.core.model.`object`

interface BiMultimap<K, V> : Iterable<Pair<K, List<V>>> {
    val size: Int
    val keySize: Int

    operator fun get(key: K): List<V>

    fun getReverse(value: V): K?

    fun hasKey(key: K): Boolean

    fun hasValue(value: V): Boolean
}

interface MutableBiMultimap<K, V> : BiMultimap<K, V> {

    operator fun set(key: K, value: V)

    fun remove(key: K)

    fun addAll(key: K, value: Iterable<V>)

    fun clear()

    fun copy(): MutableBiMultimap<K, V>
}

interface ImmutableBiMultimap<K, V> : BiMultimap<K, V> {

    operator fun set(key: K, value: V): ImmutableBiMultimap<K, V>

    fun addAll(key: K, values: Iterable<V>): ImmutableBiMultimap<K, V>

    fun remove(key: K): ImmutableBiMultimap<K, V>

    fun removeValue(key: K, value: V): ImmutableBiMultimap<K, V>

    fun clear(): ImmutableBiMultimap<K, V>
}

class ImmutableBiMultimapImpl<K, V>(
        val direct: Map<K, Set<V>>,
        val reverse: Map<V, K>
) : ImmutableBiMultimap<K, V> {

    companion object {
        fun <K, V> emptyBiMultimap(): ImmutableBiMultimap<K, V> = ImmutableBiMultimapImpl(
                emptyMap(), emptyMap())

        fun <K, V> biMultimapOf(vararg entries: Pair<K, List<V>>): ImmutableBiMultimap<K, V> {
            return entries.fold(
                    emptyBiMultimap()) { acc, entry -> acc.addAll(entry.first, entry.second) }
        }
    }

    override val keySize: Int get() = direct.size
    override val size: Int get() = reverse.size

    override fun get(key: K): List<V> = direct[key]?.toList() ?: emptyList()

    override fun getReverse(value: V): K? = reverse[value]

    override fun set(key: K, value: V): ImmutableBiMultimap<K, V> {
        return if (key in direct) {
            val set = direct.getValue(key)

            if (value in set) {
                this
            } else {
                val newDirect = direct + (key to (set + value))
                val newReverse = reverse + (value to key)
                ImmutableBiMultimapImpl(newDirect, newReverse)
            }
        } else {
            val newDirect = direct + (key to (setOf(value)))
            val newReverse = reverse + (value to key)
            ImmutableBiMultimapImpl(newDirect, newReverse)
        }
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
            return ImmutableBiMultimapImpl(newDirect, newReverse)
        }
        return this
    }

    override fun removeValue(key: K, value: V): ImmutableBiMultimap<K, V> {
        if (key in direct) {
            val values = direct.getValue(key) - value
            val newDirect = direct + (key to values)
            val newReverse = reverse - value
            return ImmutableBiMultimapImpl(newDirect, newReverse)
        }
        return this
    }

    override fun clear(): ImmutableBiMultimap<K, V> = ImmutableBiMultimapImpl(
            emptyMap(), emptyMap())

    override fun iterator(): Iterator<Pair<K, List<V>>> = direct.asSequence().map { it.key to it.value.toList() }.iterator()

    override fun hasKey(key: K): Boolean = key in direct

    override fun hasValue(value: V): Boolean = value in reverse
}