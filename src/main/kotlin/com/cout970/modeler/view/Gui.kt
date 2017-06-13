package com.cout970.modeler.view

import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.controller.CommandExecutor
import com.cout970.modeler.controller.ModelTransformer
import com.cout970.modeler.controller.ProjectController
import com.cout970.modeler.controller.selector.Selector
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
        val projectController: ProjectController,
        val selector: Selector,
        val modelTransformer: ModelTransformer
)