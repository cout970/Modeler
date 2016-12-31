package com.cout970.modeler.config

import com.cout970.modeler.log.Level

/**
 * Created by cout970 on 2016/12/27.
 */
object Config {

    var keyBindings = KeyBindings()
    var mouseTranslateSpeedX: Float = 3f
    var mouseTranslateSpeedY: Float = 3f
    var mouseRotationSpeedX: Float = 0.25f
    var mouseRotationSpeedY: Float = 0.25f
    var cameraScrollSpeed: Float = 4f
    var logLevel: Level = Level.FINE
}