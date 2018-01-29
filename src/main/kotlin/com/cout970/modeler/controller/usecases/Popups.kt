package com.cout970.modeler.controller.usecases

import com.cout970.modeler.PathConstants
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.controller.tasks.*
import com.cout970.modeler.core.export.ExportProperties
import com.cout970.modeler.core.export.ImportProperties
import com.cout970.modeler.core.model.AABB
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.GuiState
import com.cout970.modeler.gui.Popup
import com.cout970.modeler.util.toPointerBuffer
import org.lwjgl.PointerBuffer
import org.lwjgl.util.tinyfd.TinyFileDialogs
import java.io.File

val textureExtensions: PointerBuffer = listOf("*.png").toPointerBuffer()

@UseCase("project.edit")
fun showConfigMenu(gui: Gui): ITask = TaskAsync {
    gui.state.popup = Popup("config") {
        gui.state.popup = null
        gui.root.reRender()
    }
    gui.root.reRender()
}


@UseCase("model.import")
fun showImportMenu(gui: Gui, model: IModel): ITask = TaskAsync { returnCallback: (ITask) -> Unit ->
    gui.state.popup = Popup("import") { prop ->
        gui.state.popup = null
        gui.root.reRender()
        if (prop != null) {
            returnCallback(TaskImportModel(model, prop as ImportProperties))
        }
    }
    gui.root.reRender()
}

@UseCase("model.export")
fun showExportMenu(gui: Gui, model: IModel): ITask = TaskAsync { returnCallback: (ITask) -> Unit ->
    gui.state.popup = Popup("export") { prop ->
        gui.state.popup = null
        gui.root.reRender()
        if (prop != null) {
            returnCallback(TaskExportModel(model, prop as ExportProperties))
        }
    }
    gui.root.reRender()
}

@UseCase("model.export.hitboxes")
fun showExportHitboxMenu(model: IModel): ITask {
    val aabb = model.objectRefs
            .map { model.getObject(it) }
            .filter { it.visible }
            .map { AABB.fromMesh(it.mesh) }

    return TaskAsync {
        AABB.export(aabb, File(PathConstants.AABB_SAVE_FILE_PATH))
    }
}

@UseCase("texture.export")
fun showExportTextureMenu(model: IModel, guiState: GuiState): ITask = TaskAsync { returnCallback: (ITask) -> Unit ->
    val file = TinyFileDialogs.tinyfd_saveFileDialog(
            "Export Texture",
            "texture.png",
            textureExtensions,
            "PNG texture (*.png)"
    ) ?: return@TaskAsync

    val size = model.getMaterial(guiState.selectedMaterial).size

    returnCallback(TaskExportTexture(file, size, model, guiState.selectedMaterial))
}