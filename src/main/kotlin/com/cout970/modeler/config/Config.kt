package com.cout970.modeler.config

import com.cout970.modeler.log.Level
import com.cout970.modeler.project.Author

/**
 * Created by cout970 on 2016/12/27.
 */
object Config {

    var keyBindings = KeyBindings()
    val user: Author = Author()
    var mouseTranslateSpeedX: Float = 5f
    var mouseTranslateSpeedY: Float = 5f
    var mouseRotationSpeedX: Float = 0.5f
    var mouseRotationSpeedY: Float = 0.5f
    var cameraScrollSpeed: Float = 10f
    var logLevel: Level = Level.FINE
    var cursorArrowsDispersion: Float = 2f
    var cursorArrowsScale: Float = 1.0f
    val cursorArrowsSpeed: Float = 900f
    var cursorRotationSpeed: Float = 1f
    var selectionThickness: Float = 0.2f
    var perspectiveFov: Float = 45f
    var cursorLinesSize: Float = 0.5f
    var enableHelperGrid: Boolean = true
    var textureSelectionColor: Int = 0xFF_00_00 ///red
}