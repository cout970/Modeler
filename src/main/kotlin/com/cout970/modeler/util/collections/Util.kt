package com.cout970.modeler.util.collections

import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer

fun FloatArrayList.fillBuffer(floatBuffer: FloatBuffer) {
    floatBuffer.put(internalArray(), 0, size)
}

fun FloatArrayList.useAsBuffer(function: (FloatBuffer) -> Unit) {
    val buffer = MemoryUtil.memAlloc(size shl 2)
    val floatBuffer = buffer.asFloatBuffer()

    fillBuffer(floatBuffer)
    floatBuffer.flip()
    function(floatBuffer)
    MemoryUtil.memFree(buffer)
}

fun FloatArrayList.useAsBuffer(b: FloatArrayList, func: (FloatBuffer, FloatBuffer) -> Unit) {
    useAsBuffer { a ->
        b.useAsBuffer { b ->
            func(a, b)
        }
    }
}

fun FloatArrayList.useAsBuffer(b: FloatArrayList, c: FloatArrayList, func: (FloatBuffer, FloatBuffer, FloatBuffer) -> Unit) {
    useAsBuffer { a ->
        b.useAsBuffer { b ->
            c.useAsBuffer { c ->
                func(a, b, c)
            }
        }
    }
}

fun FloatArrayList.useAsBuffer(b: FloatArrayList, c: FloatArrayList, d: FloatArrayList,
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