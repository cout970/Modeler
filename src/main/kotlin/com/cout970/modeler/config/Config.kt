package com.cout970.modeler.config

import com.cout970.modeler.log.Level

/**
 * Created by cout970 on 2016/12/27.
 */
object Config {

    var keyBindings = KeyBindings()
    var mouseTranslateSpeedX: Float = 5f
    var mouseTranslateSpeedY: Float = 5f
    var mouseRotationSpeedX: Float = 0.5f
    var mouseRotationSpeedY: Float = 0.5f
    var cameraScrollSpeed: Float = 4f
    var logLevel: Level = Level.FINE
    var cursorArrowsDispersion: Float = 1f
    var cursorArrowsScale: Float = 1.0f
    var selectionThickness: Float = 0.2f
    var perspectiveFov: Float = 45f
}