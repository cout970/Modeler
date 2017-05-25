package com.cout970.modeler.util

import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer
import java.util.*

/**
 * Created by cout970 on 2017/05/17.
 */

class FloatArrayList(capacity: Int = 10) : MutableList<Float>, RandomAccess {

    private var array: FloatArray
    override var size: Int = 0
        private set
    private var modCount = 0

    init {
        array = FloatArray(capacity)
    }

    fun fillBuffer(floatBuffer: FloatBuffer) {
        floatBuffer.put(array, 0, size)
    }

    fun useAsBuffer(function: (FloatBuffer) -> Unit) {
        val buffer = MemoryUtil.memAlloc(size shl 2)
        val floatBuffer = buffer.asFloatBuffer()
        fillBuffer(floatBuffer)
        floatBuffer.flip()
        function(floatBuffer)
        MemoryUtil.memFree(buffer)
    }

    fun useAsBuffer(b: FloatArrayList, func: (FloatBuffer, FloatBuffer) -> Unit) {
        useAsBuffer { a ->
            b.useAsBuffer { b ->
                func(a, b)
            }
        }
    }

    fun useAsBuffer(b: FloatArrayList, c: FloatArrayList, func: (FloatBuffer, FloatBuffer, FloatBuffer) -> Unit) {
        useAsBuffer { a ->
            b.useAsBuffer { b ->
                c.useAsBuffer { c ->
                    func(a, b, c)
                }
            }
        }
    }

    fun useAsBuffer(b: FloatArrayList, c: FloatArrayList, d: FloatArrayList,
                    func: (FloatBuffer, FloatBuffer, FloatBuffer, FloatBuffer) -> Unit) {
        useAsBuffer { a ->
            b.useAsBuffer { b ->
                c.useAsBuffer { c ->
                    d.useAsBuffer { d ->
                        func(a, b, c, d)
                    }
                }
            }
        }
    }

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
        require(index in 0..size - 1) { "Index $index outside bounds (0, $size)" }
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
        require(index in 0..size - 1) { "Index $index outside bounds (0, $size)" }
        modCount++
        array[index] = element
    }

    override fun set(index: Int, element: Float): Float {
        require(index in 0..size - 1) { "Index $index outside bounds (0, $size)" }
        modCount++
        val old = array[index]
        array[index] = element
        return old
    }

    override fun addAll(index: Int, elements: Collection<Float>): Boolean {
        elements.forEachIndexed { index, fl ->
            if (index in 0..size - 1) {
                set(index, fl)
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
        require(fromIndex in 0..size - 1) { "FromIndex $fromIndex outside bounds (0, $size)" }
        require(toIndex in 0..size - 1) { "ToIndex $toIndex outside bounds (0, $size)" }
        return array.toMutableList().subList(fromIndex, toIndex)
    }

    private inner open class Itr : MutableIterator<Float> {
        internal var cursor: Int = 0
        internal var lastRet = -1
        internal var expectedModCount = modCount

        override fun hasNext(): Boolean {
            return cursor != size
        }

        override fun next(): Float {
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