package com.cout970.modeler.gui

import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.newSingleThreadContext
import kotlin.coroutines.experimental.CoroutineContext

val UI = newSingleThreadContext("UI")

data class Popup(val name: String, val returnFunc: (Any?) -> Unit)

fun CoroutineContext.setTimeout(time: Int, func: () -> Unit) {
    launch(this) {
        delay(time.toLong())
        func()
    }
}