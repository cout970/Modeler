package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.controller.injection.Inject
import com.cout970.modeler.controller.tasks.*
import com.cout970.modeler.core.model.AABB
import com.cout970.modeler.gui.GuiState
import com.cout970.modeler.gui.dialogs.ExportDialog
import com.cout970.modeler.gui.dialogs.ImportDialog
import com.cout970.modeler.util.toPointerBuffer
import org.lwjgl.PointerBuffer
import org.lwjgl.util.tinyfd.TinyFileDialogs
import java.io.File

/**
 * Created by cout970 on 2017/07/19.
 */

val textureExtensions: PointerBuffer = listOf("*.png").toPointerBuffer()

class ImportModel : IUseCase {

    override val key: String = "model.import"

    @Inject lateinit var model: IModel

    override fun createTask(): ITask {
        val callback = { returnCallback: (ITask) -> Unit ->
            ImportDialog.show { prop ->
                if (prop != null) {
                    returnCallback(TaskImportModel(model, prop))
                }
            }
        }
        return TaskCallback(callback)
    }
}

class ExportModel : IUseCase {

    override val key: String = "model.export"

    @Inject lateinit var model: IModel

    override fun createTask(): ITask {
        val callback = { returnCallback: (ITask) -> Unit ->
            ExportDialog.show { prop ->
                if (prop != null) {
                    returnCallback(TaskExportModel(model, prop))
                }
            }
        }
        return TaskCallback(callback)
    }
}

class ExportHitboxes : IUseCase {

    override val key: String = "model.export.hitboxes"

    @Inject lateinit var model: IModel

    override fun createTask(): ITask {
        val aabb = model.objectRefs
                .filter { model.isVisible(it) }
                .map { model.getObject(it) }
                .map { AABB.fromMesh(it.mesh) }

        AABB.export(aabb, File("./saves", "aabb.txt"))
        return TaskNone
    }
}

class ExportTexture : IUseCase {

    override val key: String = "texture.export"

    @Inject lateinit var model: IModel
    @Inject lateinit var guiState: GuiState

    override fun createTask(): ITask {
        val file = TinyFileDialogs.tinyfd_saveFileDialog("Export Texture", "texture.png",
                textureExtensions, "PNG texture (*.png)") ?: return TaskNone

        val size = model.getMaterial(guiState.selectedMaterial).size

        return TaskExportTexture(file, size, model, guiState.selectedMaterial)
    }
}
