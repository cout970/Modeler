package com.cout970.modeler.controller.usecases

import com.cout970.modeler.PathConstants
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.controller.tasks.*
import com.cout970.modeler.core.export.ExportProperties
import com.cout970.modeler.core.export.ExportTextureProperties
import com.cout970.modeler.core.export.ImportProperties
import com.cout970.modeler.core.model.AABB
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.Popup
import com.cout970.reactive.core.AsyncManager
import com.cout970.vector.extensions.vec2Of
import java.io.File


@UseCase("project.edit")
private fun showConfigMenu(gui: Gui): ITask = TaskAsync {
    gui.state.popup = Popup("config") {
        gui.state.popup = null
        AsyncManager.runLater { gui.root.reRender() }
    }
    AsyncManager.runLater { gui.root.reRender() }
}


@UseCase("model.import")
private fun showImportMenu(gui: Gui, model: IModel): ITask = TaskAsync { returnCallback: (ITask) -> Unit ->
    gui.state.popup = Popup("import") { prop ->
        gui.state.popup = null
        AsyncManager.runLater { gui.root.reRender() }
        if (prop != null) {
            returnCallback(TaskImportModel(model, prop as ImportProperties))
        }
    }
    AsyncManager.runLater { gui.root.reRender() }
}

@UseCase("model.export")
private fun showExportMenu(gui: Gui, model: IModel): ITask = TaskAsync { returnCallback: (ITask) -> Unit ->
    gui.state.popup = Popup("export") { prop ->
        gui.state.popup = null
        AsyncManager.runLater { gui.root.reRender() }
        if (prop != null) {
            returnCallback(TaskExportModel(model, prop as ExportProperties))
        }
    }
    AsyncManager.runLater { gui.root.reRender() }
}

@UseCase("model.export.hitboxes")
private fun showExportHitboxMenu(model: IModel): ITask {
    val aabb = model.objectRefs
            .map { model.getObject(it) }
            .filter { it.visible }
            .map { AABB.fromMesh(it.mesh) }

    return TaskAsync {
        AABB.export(aabb, File(PathConstants.AABB_SAVE_FILE_PATH))
    }
}

@UseCase("texture.export")
private fun showExportTextureMenu(model: IModel, gui: Gui): ITask = TaskAsync { returnCallback: (ITask) -> Unit ->
    val guiState = gui.state

    guiState.popup = Popup("export_texture") {
        guiState.popup = null

        AsyncManager.runLater { gui.root.reRender() }

        (it as? ExportTextureProperties)?.let { props ->
            returnCallback(TaskExportTexture(props.path, vec2Of(props.size), model, guiState.selectedMaterial))
        }
    }
    AsyncManager.runLater { gui.root.reRender() }
}