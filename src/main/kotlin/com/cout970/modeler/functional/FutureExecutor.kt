package com.cout970.modeler.functional

import com.cout970.modeler.ProgramState
import com.cout970.modeler.util.ITickeable
import java.util.*

/**
 * Created by cout970 on 2017/07/17.
 */

interface IFutureExecutor {
    fun addToQueue(function: (ProgramState) -> Unit)
}

class FutureExecutor : ITickeable, IFutureExecutor {

    lateinit var programState: ProgramState
    private val queue = LinkedList<(ProgramState) -> Unit>()

    override fun addToQueue(function: (ProgramState) -> Unit) {
        queue.add(function)
    }

    override fun tick() = Unit

    override fun postTick() {
        while (queue.isNotEmpty()) {
            queue.poll().invoke(programState)
        }
    }
}