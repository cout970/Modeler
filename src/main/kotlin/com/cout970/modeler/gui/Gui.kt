package com.cout970.modeler.gui

import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.controller.Dispatcher
import com.cout970.modeler.controller.SelectionHandler
import com.cout970.modeler.controller.binders.ButtonBinder
import com.cout970.modeler.controller.binders.KeyboardBinder
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.gui.canvas.CanvasContainer
import com.cout970.modeler.gui.canvas.CanvasManager
import com.cout970.modeler.gui.editor.EditorPanel
import com.cout970.modeler.input.event.IInput
import com.cout970.modeler.input.window.WindowHandler

/**
 * Created by cout970 on 2017/05/26.
 */

data class Gui(
        val root: Root,
        val guiUpdater: GuiUpdater,
        val canvasContainer: CanvasContainer,
        val listeners: Listeners,
        val windowHandler: WindowHandler,
        val timer: Timer,
        val input: IInput,
        var editorPanel: EditorPanel,
        val projectManager: ProjectManager,
        val resources: GuiResources,
        val state: GuiState,
        val selectionHandler: SelectionHandler = SelectionHandler(),
        val dispatcher: Dispatcher,
        val buttonBinder: ButtonBinder,
        val keyboardBinder: KeyboardBinder,
        val canvasManager: CanvasManager
) {

    init {
        guiUpdater.initGui(this)
        canvasManager.gui = this
    }
}