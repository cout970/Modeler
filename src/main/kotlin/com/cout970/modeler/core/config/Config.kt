package com.cout970.modeler.core.config

import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.project.Author

/**
 * Created by cout970 on 2016/12/27.
 */
object Config {

    var keyBindings = KeyBindings()
    var user: Author = Author()
    var colorPalette = ColorPalette.defaultPalette
    var mouseTranslateSpeedX: Float = 5f
    var mouseTranslateSpeedY: Float = 5f
    var mouseRotationSpeedX: Float = 0.5f
    var mouseRotationSpeedY: Float = 0.5f
    var cameraScrollSpeed: Float = 10f
    var logLevel: Level = Level.FINE
    var cursorArrowsDispersion: Float = 2f
    var cursorArrowsScale: Float = 0.75f
    var cursorArrowsSpeed: Float = 900f
    var cursorRotationSpeed: Float = 1f
    var selectionThickness: Float = 0.2f
    var perspectiveFov: Float = 45f
    var enableHelperGrid: Boolean = true
}