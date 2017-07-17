package com.cout970.modeler.view

import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.controller.ActionExecutor
import com.cout970.modeler.controller.CommandExecutor
import com.cout970.modeler.controller.GuiState
import com.cout970.modeler.controller.SelectionHandler
import com.cout970.modeler.controller.selector.Selector
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.functional.Dispatcher
import com.cout970.modeler.functional.binders.ButtonBinder
import com.cout970.modeler.view.event.IInput
import com.cout970.modeler.view.gui.GuiUpdater
import com.cout970.modeler.view.gui.Root
import com.cout970.modeler.view.gui.comp.canvas.CanvasContainer
import com.cout970.modeler.view.gui.editor.EditorPanel
import com.cout970.modeler.view.window.WindowHandler

/**
 * Created by cout970 on 2017/05/26.
 */

data class Gui(
        val root: Root,
        val guiUpdater: GuiUpdater,
        val canvasContainer: CanvasContainer,
        val commandExecutor: CommandExecutor,
        val listeners: Listeners,
        val windowHandler: WindowHandler,
        val timer: Timer,
        val input: IInput,
        val editorPanel: EditorPanel,
        val projectManager: ProjectManager,
        val selector: Selector,
        val actionExecutor: ActionExecutor,
        val resources: GuiResources,
        val state: GuiState,
        val selectionHandler: SelectionHandler = SelectionHandler(),
        val dispatcher: Dispatcher,
        val buttonBinder: ButtonBinder
) {

    init {
        guiUpdater.initGui(this)
        selector.gui = this
    }
}