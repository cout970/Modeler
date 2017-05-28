package com.cout970.modeler.view

import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.controller.CommandExecutor
import com.cout970.modeler.view.gui.GuiUpdater
import com.cout970.modeler.view.gui.Root
import com.cout970.modeler.view.gui.canvas.CanvasContainer
import com.cout970.modeler.view.window.WindowHandler

/**
 * Created by cout970 on 2017/05/26.
 */

data class GuiState(
        val root: Root,
        val guiUpdater: GuiUpdater,
        val canvasContainer: CanvasContainer,
        val commandExecutor: CommandExecutor,
        val listeners: Listeners,
        val windowHandler: WindowHandler,
        val timer: Timer
)