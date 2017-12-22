package com.cout970.modeler.gui

import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.controller.Dispatcher
import com.cout970.modeler.controller.binders.ButtonBinder
import com.cout970.modeler.controller.binders.KeyboardBinder
import com.cout970.modeler.core.project.IModelAccessor
import com.cout970.modeler.core.project.IProjectPropertiesHolder
import com.cout970.modeler.gui.canvas.CanvasContainer
import com.cout970.modeler.gui.canvas.CanvasManager
import com.cout970.modeler.gui.canvas.cursor.CursorManager
import com.cout970.modeler.gui.views.EditorView
import com.cout970.modeler.input.event.IInput
import com.cout970.modeler.input.window.WindowHandler

/**
 * Created by cout970 on 2017/05/26.
 */

data class Gui(
        val root: Root,
        val canvasContainer: CanvasContainer,
        val listeners: Listeners,
        val windowHandler: WindowHandler,
        val timer: Timer,
        val input: IInput,
        var editorView: EditorView,
        val resources: GuiResources,
        val state: GuiState,
        val modelAccessor: IModelAccessor,
        val dispatcher: Dispatcher,
        val buttonBinder: ButtonBinder,
        val keyboardBinder: KeyboardBinder,
        val canvasManager: CanvasManager,
        val cursorManager: CursorManager,
        val propertyHolder: IProjectPropertiesHolder
) {

    init {
        canvasManager.gui = this
        editorView.gui = this
    }
}