package com.cout970.modeler.view

import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.functional.Dispatcher
import com.cout970.modeler.functional.SelectionHandler
import com.cout970.modeler.functional.binders.ButtonBinder
import com.cout970.modeler.functional.binders.KeyboardBinder
import com.cout970.modeler.view.canvas.CanvasContainer
import com.cout970.modeler.view.canvas.CanvasManager
import com.cout970.modeler.view.canvas.Selector
import com.cout970.modeler.view.event.IInput
import com.cout970.modeler.view.gui.GuiUpdater
import com.cout970.modeler.view.gui.Root
import com.cout970.modeler.view.gui.editor.EditorPanel
import com.cout970.modeler.view.window.WindowHandler

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
        val selector: Selector,
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
        selector.gui = this
        canvasManager.gui = this
    }
}