package com.cout970.modeler.gui

import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.newSingleThreadContext
import kotlin.coroutines.experimental.CoroutineContext

// Async ui operations like open a popup, notifications or loading a screen
val UI = newSingleThreadContext("UI")

// Heavy computations, model transformations and things that take long enough to reduce the fps
val COMPUTE = newSingleThreadContext("COMPUTE")

data class Popup(val name: String, val returnFunc: (Any?) -> Unit)

fun CoroutineContext.setTimeout(time: Int, func: () -> Unit) {
    launch(this) {
        delay(time.toLong())
        func()
    }
}

fun CoroutineContext.runAsync(func: suspend () -> Unit) {
    launch(this) { func() }
}

