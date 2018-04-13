package com.cout970.modeler.util.collections

import java.util.*

/**
 * Created by cout970 on 2017/05/17.
 */

fun listOf(vararg values: Float) = FloatArrayList(values)

class FloatArrayList internal constructor(private var array: FloatArray) : MutableList<Float>, RandomAccess {

    override var size: Int = 0
        private set
    private var modCount = 0

    constructor(capacity: Int = 10) : this(FloatArray(capacity))

    internal fun internalArray(): FloatArray = array

    override fun contains(element: Float): Boolean {
        repeat(size) {
            if (array[it] == element) return true
        }
        return false
    }

    override fun containsAll(elements: Collection<Float>): Boolean {
        return elements.all { contains(it) }
    }

    override fun get(index: Int): Float {
        require(index in 0 until size) { "Index $index outside bounds (0, $size)" }
        return array[index]
    }

    override fun indexOf(element: Float): Int {
        repeat(size) {
            if (array[it] == element) return it
        }
        return -1
    }

    override fun isEmpty(): Boolean = size == 0

    override fun iterator(): MutableIterator<Float> = Itr()

    override fun lastIndexOf(element: Float): Int {
        repeat(size) { i ->
            val index = (size - 1) - i
            if (array[index] == element) return index
        }
        return -1
    }

    override fun add(element: Float): Boolean {
        modCount++
        if (array.size == size) {
            growArray()
        }
        array[size] = element
        size++
        return true
    }

    private fun growArray() {
        val newArray = FloatArray(array.size * 2)
        System.arraycopy(array, 0, newArray, 0, array.size)
        array = newArray
    }

    override fun add(index: Int, element: Float) {
        require(index in 0 until size) { "Index $index outside bounds (0, $size)" }
        modCount++
        array[index] = element
    }

    override fun set(index: Int, element: Float): Float {
        require(index in 0 until size) { "Index $index outside bounds (0, $size)" }
        modCount++
        val old = array[index]
        array[index] = element
        return old
    }

    override fun addAll(index: Int, elements: Collection<Float>): Boolean {
        elements.forEachIndexed { pos, fl ->
            if (pos + index in 0 until size) {
                set(pos + index, fl)
            } else {
                add(fl)
            }
        }
        return true
    }

    override fun addAll(elements: Collection<Float>): Boolean {
        elements.forEach { add(it) }
        return true
    }

    override fun clear() {
        modCount++
        size = 0
    }

    override fun listIterator(): MutableListIterator<Float> = listIterator(0)

    override fun listIterator(index: Int): MutableListIterator<Float> = ListItr(index)

    override fun remove(element: Float): Boolean {
        val index = indexOf(element)
        if (index != -1) {
            removeAt(index)
            return true
        }
        return false
    }

    override fun removeAll(elements: Collection<Float>): Boolean {
        return elements.map { remove(it) }.any()
    }

    override fun removeAt(index: Int): Float {
        modCount++
        val oldValue = array[index]

        val numMoved = size - index - 1
        if (numMoved > 0)
            System.arraycopy(array, index + 1, array, index, numMoved)
        size--

        return oldValue
    }

    override fun retainAll(elements: Collection<Float>): Boolean {
        val oldSize = size
        elements.forEach { remove(it) }
        return oldSize != size
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<Float> {
        require(fromIndex in 0 until size) { "FromIndex $fromIndex outside bounds [0, $size)" }
        require(toIndex in 0 until size) { "ToIndex $toIndex outside bounds [0, $size)" }
        return array.toMutableList().subList(fromIndex, toIndex)
    }

    private open inner class Itr : MutableIterator<Float>, FloatIterator() {
        internal var cursor: Int = 0
        internal var lastRet = -1
        internal var expectedModCount = modCount

        override fun hasNext(): Boolean {
            return cursor != size
        }

        override fun nextFloat(): Float {
            checkForComodification()
            val i = cursor
            if (i >= size)
                throw NoSuchElementException()
            val elementData = array
            if (i >= elementData.size)
                throw ConcurrentModificationException()
            cursor = i + 1
            lastRet = i
            return elementData[i]
        }

        override fun remove() {
            if (lastRet < 0)
                throw IllegalStateException()
            checkForComodification()

            try {
                this@FloatArrayList.removeAt(lastRet)
                cursor = lastRet
                lastRet = -1
                expectedModCount = modCount
            } catch (ex: IndexOutOfBoundsException) {
                throw ConcurrentModificationException()
            }
        }

        internal fun checkForComodification() {
            if (modCount != expectedModCount)
                throw ConcurrentModificationException()
        }
    }

    private inner class ListItr internal constructor(index: Int) : Itr(), MutableListIterator<Float> {
        init {
            cursor = index
        }

        override fun hasPrevious(): Boolean {
            return cursor != 0
        }

        override fun previous(): Float {
            checkForComodification()
            try {
                val i = cursor - 1
                val previous = get(i)
                cursor = i
                lastRet = cursor
                return previous
            } catch (e: IndexOutOfBoundsException) {
                checkForComodification()
                throw NoSuchElementException()
            }
        }

        override fun nextIndex(): Int {
            return cursor
        }

        override fun previousIndex(): Int {
            return cursor - 1
        }

        override fun set(element: Float) {
            if (lastRet < 0)
                throw IllegalStateException()
            checkForComodification()

            try {
                this@FloatArrayList[lastRet] = element
                expectedModCount = modCount
            } catch (ex: IndexOutOfBoundsException) {
                throw ConcurrentModificationException()
            }
        }

        override fun add(element: Float) {
            checkForComodification()

            try {
                val i = cursor
                this@FloatArrayList.add(i, element)
                lastRet = -1
                cursor = i + 1
                expectedModCount = modCount
            } catch (ex: IndexOutOfBoundsException) {
                throw ConcurrentModificationException()
            }
        }
    }
}