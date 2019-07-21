package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.controller.tasks.*
import com.cout970.modeler.core.config.colorToHex
import com.cout970.modeler.core.model.material.ColoredMaterial
import com.cout970.modeler.core.model.material.MaterialNone
import com.cout970.modeler.core.model.material.MaterialRefNone
import com.cout970.modeler.core.model.material.TexturedMaterial
import com.cout970.modeler.core.model.objects
import com.cout970.modeler.core.model.selection.Selection
import com.cout970.modeler.core.project.IProgramState
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.gui.GuiState
import com.cout970.modeler.input.dialogs.FileDialogs
import com.cout970.modeler.input.dialogs.MessageDialogs
import com.cout970.modeler.util.asNullable
import com.cout970.modeler.util.getOr
import com.cout970.modeler.util.toResourcePath
import com.cout970.vector.extensions.vec3Of
import org.liquidengine.legui.component.Component
import java.awt.Color
import java.io.File

/**
 * Created by cout970 on 2017/07/20.
 */

@UseCase("material.view.apply")
private fun applyMaterial(component: Component, programState: IProgramState): ITask {
    val model = programState.model
    programState.modelSelection.ifNotNull { selection ->
        component.asNullable()
                .map { it.metadata["ref"] }
                .flatMap { it as? IMaterialRef }
                .map { ref ->
                    val newModel = model.modifyObjects(selection.objects.toSet()) { _, obj ->
                        obj.withMaterial(ref)
                    }
                    return TaskUpdateModel(oldModel = model, newModel = newModel)
                }
    }
    return TaskNone
}

@UseCase("material.view.load")
private fun loadMaterial(component: Component, projectManager: ProjectManager): ITask {
    val ref = component.metadata["ref"] ?: return TaskNone
    val materialRef = ref as? IMaterialRef ?: return TaskNone

    return if (materialRef == MaterialRefNone) {
        importMaterial()
    } else {
        showLoadMaterialMenu(materialRef, projectManager)
    }
}

private fun showLoadMaterialMenu(ref: IMaterialRef, projectManager: ProjectManager): ITask = TaskAsync { returnFunc ->
    val path = FileDialogs.openFile(
            title = "Import Texture",
            description = "PNG texture (*.png)",
            filters = listOf("*.png")
    )
    if (path != null) {

        val archive = File(path)
        val material = TexturedMaterial(archive.nameWithoutExtension, archive.toResourcePath(), ref.materialId)
        returnFunc(TaskUpdateMaterial(
                oldMaterial = projectManager.loadedMaterials[ref]!!,
                newMaterial = material
        ))
    }
}

@UseCase("material.view.import")
private fun importMaterial(): ITask = TaskAsync { returnFunc ->
    val file = FileDialogs.openFile(
            title = "Import Texture",
            description = "PNG texture (*.png)",
            filters = listOf("*.png")
    )

    if (file != null) {
        val archive = File(file)
        val material = TexturedMaterial(archive.nameWithoutExtension, archive.toResourcePath())
        returnFunc(TaskImportMaterial(material))
    }
}

@UseCase("material.new.colored")
private fun newColoredMaterial(): ITask = TaskAsync { returnFunc ->

    val c = Color.getHSBColor(Math.random().toFloat(), 0.5f, 1.0f)
    val color = vec3Of(c.red / 255f, c.green / 255f, c.blue / 255f)
    val name = "Color #${colorToHex(color)}"

    returnFunc(TaskImportMaterial(ColoredMaterial(name, color)))
}

@UseCase("material.view.select")
private fun selectMaterial(component: Component): ITask {
    return component.asNullable()
            .map { it.metadata["ref"] }
            .flatMap { it as? IMaterialRef }
            .map { TaskUpdateMaterialSelection(it) as ITask }
            .getOr(TaskNone)
}

@UseCase("material.view.duplicate")
private fun duplicateMaterial(component: Component, access: IProgramState): ITask {
    return component.asNullable()
            .flatMap { it.metadata["ref"] }
            .flatMap { it as? IMaterialRef }
            .map { access.model.getMaterial(it) }
            .filterIsInstance<TexturedMaterial>()
            .map { TaskImportMaterial(it.copy(name = "${it.name} copy")) as ITask }
            .getOr(TaskNone)
}

@UseCase("material.view.remove")
private fun removeMaterial(guiState: GuiState, projectManager: ProjectManager): ITask {
    val matRef = guiState.selectedMaterial
    val model = projectManager.model
    val material = model.getMaterial(matRef)

    if (material == MaterialNone) return TaskNone

    val used = model.objects.any { it.material == matRef }

    if (used) {
        //ask
        return TaskAsync { returnFunc ->
            val result = MessageDialogs.warningBoolean(
                    title = "Remove material",
                    message = "Are you sure you want to remove this material?",
                    default = false
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
            TaskRemoveMaterial(ref, material)
    ))
}


@UseCase("material.view.inverse_select")
private fun selectByMaterial(guiState: GuiState, programState: IProgramState): ITask {
    val matRef = guiState.selectedMaterial
    val model = programState.model

    val objs = model.objectMap.entries
            .filter { it.value.material == matRef }
            .map { it.key }

    return TaskUpdateModelSelection(
            newSelection = Selection(SelectionTarget.MODEL, SelectionType.OBJECT, objs).asNullable(),
            oldSelection = programState.modelSelection
    )
}