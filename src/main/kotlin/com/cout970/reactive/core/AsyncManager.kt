package com.cout970.reactive.core

import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.newSingleThreadContext
import java.util.*
import kotlin.coroutines.experimental.CoroutineContext

interface IAsyncManager {
    fun runLater(function: () -> Unit)
}

object AsyncManager : IAsyncManager {

    private var instance: IAsyncManager = SyncManager

    fun setInstance(manager: IAsyncManager) {
        instance = manager
    }

    override fun runLater(function: () -> Unit) {
        instance.runLater(function)
    }
}

object SyncManager : IAsyncManager {

    private val taskQueue = mutableListOf<() -> Unit>()

    fun runSync() {
        taskQueue.removeAll { it(); true }
    }

    override fun runLater(function: () -> Unit) {
        taskQueue.add(function)
    }
}

object CoroutineAsyncManager : IAsyncManager {

    private var updateCtx: CoroutineContext = newSingleThreadContext("Reactive")
    private val intervals = Collections.synchronizedList(mutableListOf<Interval>())


    override fun runLater(function: () -> Unit) {
        setTimeout(0, function)
    }

    fun setTimeout(time: Int, func: () -> Unit) {
        launch(updateCtx) {
            delay(time.toLong())
            func()
        }
    }

    fun setInterval(milliseconds: Int, func: () -> Unit): Interval {
        return Interval(milliseconds, func).also {
            intervals.add(it)

            launch(updateCtx) {
                while (true) {
                    delay(it.time)
                    if (it in intervals) {
                        it.func()
                    } else {
                        break
                    }
                }
            }
        }
    }

    fun clearInterval(interval: Interval) {
        intervals.remove(interval)
    }

    class Interval(val time: Int, val func: () -> Unit)
}