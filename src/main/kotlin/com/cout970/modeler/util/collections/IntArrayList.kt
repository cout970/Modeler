package com.cout970.modeler.util.collections

import java.util.*

/**
 * Created by cout970 on 2018/04/9.
 */

fun listOf(vararg values: Int) = IntArrayList(values)

class IntArrayList internal constructor(private var array: IntArray) : MutableList<Int>, RandomAccess {

    override var size: Int = 0
        private set
    private var modCount = 0

    constructor(capacity: Int = 10) : this(IntArray(capacity))

    internal fun internalArray(): IntArray = array

    override fun contains(element: Int): Boolean {
        repeat(size) {
            if (array[it] == element) return true
        }
        return false
    }

    override fun containsAll(elements: Collection<Int>): Boolean {
        return elements.all { contains(it) }
    }

    override fun get(index: Int): Int {
        require(index in 0 until size) { "Index $index outside bounds (0, $size)" }
        return array[index]
    }

    override fun indexOf(element: Int): Int {
        repeat(size) {
            if (array[it] == element) return it
        }
        return -1
    }

    override fun isEmpty(): Boolean = size == 0

    override fun iterator(): MutableIterator<Int> = Itr()

    override fun lastIndexOf(element: Int): Int {
        repeat(size) { i ->
            val index = (size - 1) - i
            if (array[index] == element) return index
        }
        return -1
    }

    override fun add(element: Int): Boolean {
        modCount++
        if (array.size == size) {
            growArray()
        }
        array[size] = element
        size++
        return true
    }

    private fun growArray() {
        val newArray = IntArray(array.size * 2)
        System.arraycopy(array, 0, newArray, 0, array.size)
        array = newArray
    }

    override fun add(index: Int, element: Int) {
        require(index in 0 until size) { "Index $index outside bounds (0, $size)" }
        modCount++
        array[index] = element
    }

    override fun set(index: Int, element: Int): Int {
        require(index in 0 until size) { "Index $index outside bounds (0, $size)" }
        modCount++
        val old = array[index]
        array[index] = element
        return old
    }

    override fun addAll(index: Int, elements: Collection<Int>): Boolean {
        elements.forEachIndexed { pos, fl ->
            if (pos + index in 0 until size) {
                set(pos + index, fl)
            } else {
                add(fl)
            }
        }
        return true
    }

    override fun addAll(elements: Collection<Int>): Boolean {
        elements.forEach { add(it) }
        return true
    }

    override fun clear() {
        modCount++
        size = 0
    }

    override fun listIterator(): MutableListIterator<Int> = listIterator(0)

    override fun listIterator(index: Int): MutableListIterator<Int> = ListItr(index)

    override fun remove(element: Int): Boolean {
        val index = indexOf(element)
        if (index != -1) {
            removeAt(index)
            return true
        }
        return false
    }

    override fun removeAll(elements: Collection<Int>): Boolean {
        return elements.map { remove(it) }.any()
    }

    override fun removeAt(index: Int): Int {
        modCount++
        val oldValue = array[index]

        val numMoved = size - index - 1
        if (numMoved > 0)
            System.arraycopy(array, index + 1, array, index, numMoved)
        size--

        return oldValue
    }

    override fun retainAll(elements: Collection<Int>): Boolean {
        val oldSize = size
        elements.forEach { remove(it) }
        return oldSize != size
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<Int> {
        require(fromIndex in 0 until size) { "FromIndex $fromIndex outside bounds [0, $size)" }
        require(toIndex in 0 until size) { "ToIndex $toIndex outside bounds [0, $size)" }
        return array.toMutableList().subList(fromIndex, toIndex)
    }

    private open inner class Itr : MutableIterator<Int>, IntIterator() {
        internal var cursor: Int = 0
        internal var lastRet = -1
        internal var expectedModCount = modCount

        override fun hasNext(): Boolean {
            return cursor != size
        }

        override fun nextInt(): Int {
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
                this@IntArrayList.removeAt(lastRet)
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

    private inner class ListItr internal constructor(index: Int) : Itr(), MutableListIterator<Int> {
        init {
            cursor = index
        }

        override fun hasPrevious(): Boolean {
            return cursor != 0
        }

        override fun previous(): Int {
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

        override fun set(element: Int) {
            if (lastRet < 0)
                throw IllegalStateException()
            checkForComodification()

            try {
                this@IntArrayList[lastRet] = element
                expectedModCount = modCount
            } catch (ex: IndexOutOfBoundsException) {
                throw ConcurrentModificationException()
            }
        }

        override fun add(element: Int) {
            checkForComodification()

            try {
                val i = cursor
                this@IntArrayList.add(i, element)
                lastRet = -1
                cursor = i + 1
                expectedModCount = modCount
            } catch (ex: IndexOutOfBoundsException) {
                throw ConcurrentModificationException()
            }
        }
    }
}