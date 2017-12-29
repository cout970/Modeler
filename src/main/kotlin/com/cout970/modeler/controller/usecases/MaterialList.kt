package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.controller.tasks.*
import com.cout970.modeler.core.model.getSelectedObjectRefs
import com.cout970.modeler.core.model.material.MaterialNone
import com.cout970.modeler.core.model.material.MaterialRefNone
import com.cout970.modeler.core.model.material.TexturedMaterial
import com.cout970.modeler.core.project.ModelAccessor
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.gui.GuiState
import com.cout970.modeler.util.asNullable
import com.cout970.modeler.util.toResourcePath
import org.liquidengine.legui.component.Component
import org.lwjgl.util.tinyfd.TinyFileDialogs
import java.io.File

/**
 * Created by cout970 on 2017/07/20.
 */

@UseCase("material.view.apply")
fun applyMaterial(component: Component, modelAccessor: ModelAccessor): ITask {
    val model = modelAccessor.model
    modelAccessor.modelSelection.ifNotNull { selection ->
        component.asNullable()
                .map { it.metadata["ref"] }
                .flatMap { it as? IMaterialRef }
                .map { ref ->
                    val newModel = model.modifyObjects(model.getSelectedObjectRefs(selection)) { _, obj ->
                        obj.withMaterial(ref)
                    }
                    TaskUpdateModel(oldModel = model, newModel = newModel) as ITask
                }
                .getOr(TaskNone)
    }
    return TaskNone
}

@UseCase("material.view.load")
fun loadMaterial(component: Component, projectManager: ProjectManager): ITask {
    return component
            .asNullable()
            .map { it.metadata["ref"] }
            .flatMap { it as? IMaterialRef }
            .map { showLoadMaterialMenu(it, projectManager) }
            .getOr(TaskNone)
}

private fun showLoadMaterialMenu(ref: IMaterialRef, projectManager: ProjectManager): ITask = TaskAsync { returnFunc ->
    val path = TinyFileDialogs.tinyfd_openFileDialog(
            "Import Texture",
            "",
            textureExtensions,
            "PNG texture (*.png)",
            false
    )
    if (path != null) {

        val archive = File(path)
        val material = TexturedMaterial(archive.nameWithoutExtension, archive.toResourcePath())
        returnFunc(TaskUpdateMaterial(
                ref = ref,
                oldMaterial = projectManager.loadedMaterials[ref.materialIndex],
                newMaterial = material
        ))
    }
}

@UseCase("material.view.import")
fun importMaterial(projectManager: ProjectManager): ITask = TaskAsync { returnFunc ->
    val file = TinyFileDialogs.tinyfd_openFileDialog("Import Texture", "",
            textureExtensions, "PNG texture (*.png)", false)

    if (file != null) {
        val archive = File(file)
        val material = TexturedMaterial(archive.nameWithoutExtension, archive.toResourcePath())
        returnFunc(TaskImportMaterial(material))
    }
}

@UseCase("material.view.select")
fun selectMaterial(component: Component): ITask {
    return component.asNullable()
            .map { it.metadata["ref"] }
            .flatMap { it as? IMaterialRef }
            .map { TaskUpdateMaterialSelection(it) as ITask }
            .getOr(TaskNone)
}

@UseCase("material.view.remove")
fun removeMaterial(guiState: GuiState, projectManager: ProjectManager): ITask {
    val matRef = guiState.selectedMaterial
    val model = projectManager.model
    val material = model.getMaterial(matRef)

    if (material is MaterialNone) return TaskNone

    val used = model.objects.any { it.material == matRef }

    if (used) {
        //ask
        return TaskAsync { returnFunc ->
            val result = TinyFileDialogs.tinyfd_messageBox(
                    "Remove material",
                    "Are you sure you want to remove this material?",
                    "yesno",
                    "warning",
                    false
            )
            if (result) {
                returnFunc(removeMaterialTask(model, matRef, material))
            }
        }
    }

    return removeMaterialTask(model, matRef, material)
}

private fun removeMaterialTask(model: IModel, ref: IMaterialRef, material: IMaterial): ITask {
    val newModel = model.modifyObjects({ true }) { _, obj ->
        if (obj.material == ref) obj.withMaterial(MaterialRefNone) else obj
    }
    return TaskChain(listOf(
            TaskUpdateModel(oldModel = model, newModel = newModel),
            TaskRemoveMaterial(material)
    ))
}
