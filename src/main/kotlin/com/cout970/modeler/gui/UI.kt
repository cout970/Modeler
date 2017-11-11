package com.cout970.modeler.gui

import kotlinx.coroutines.experimental.newSingleThreadContext

val UI = newSingleThreadContext("UI")

data class Popup(val name: String, val returnFunc: (Any?) -> Unit)