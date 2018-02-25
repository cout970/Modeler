package com.cout970.modeler.core.config

import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.project.Author

/**
 * Created by cout970 on 2016/12/27.
 */
object Config {

    var keyBindings = KeyBindings()
    var user: Author = Author()
    var logLevel: Level = Level.FINE
    var colorPalette = ColorPalette.defaultPalette

    var backupPath: String = "data/backups"

    @ConfigComment("Speed moving the camera in the X axis")
    var mouseTranslateSpeedX: Float = 3.0f

    @ConfigComment("Speed moving the camera in the Y axis")
    var mouseTranslateSpeedY: Float = 3.0f

    @ConfigComment("Speed rotating the camera when the mouse moves in the X axis")
    var mouseRotationSpeedX: Float = 0.2f

    @ConfigComment("Speed rotating the camera when the mouse moves in the Y axis")
    var mouseRotationSpeedY: Float = 0.3f

    @ConfigComment("Speed changing the zoom with the mouse scroll")
    var cameraScrollSpeed: Float = 10f

    @ConfigComment("Distance from the cursor center to the end")
    var cursorArrowsDispersion: Float = 2.0f

    @ConfigComment("Total size of the cursor arrows")
    var cursorArrowsScale: Float = 0.75f

    @ConfigComment("Speed moving things using the cursor arrows")
    var cursorArrowsSpeed: Float = 900f

    @ConfigComment("Speed rotating things using the cursor arrows")
    var cursorRotationSpeed: Float = 1.0f

    @ConfigComment("Thickness in pixels of the selection mark")
    var selectionThickness: Float = 0.2f

    @ConfigComment("Field of view")
    var perspectiveFov: Float = 45f

    @ConfigComment("Zoom value before the grid changes from pixels to blocks")
    var zoomLevelToChangeGridDetail: Float = 135f

    @ConfigComment("Amount of milliseconds between backups")
    var backupInterval: Int = 60_000
}