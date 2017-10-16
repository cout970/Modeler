package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.controller.injection.Inject
import com.cout970.modeler.controller.tasks.*
import com.cout970.modeler.core.model.getSelectedObjectRefs
import com.cout970.modeler.core.model.material.TexturedMaterial
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.resource.toResourcePath
import com.cout970.modeler.util.Nullable
import com.cout970.modeler.util.asNullable
import com.cout970.modeler.util.toNullable
import org.liquidengine.legui.component.Component
import org.lwjgl.util.tinyfd.TinyFileDialogs
import java.io.File

/**
 * Created by cout970 on 2017/07/20.
 */

class ApplyMaterial : IUseCase {

    override val key: String = "material.view.apply"

    @Inject lateinit var component: Component
    @Inject lateinit var model: IModel
    @Inject lateinit var selection: Nullable<ISelection>

    override fun createTask(): ITask {
        return selection
                .toNullable()
                .map { selection ->
                    component.asNullable()
                            .map { it.metadata["ref"] }
                            .flatMap { it as? IMaterialRef }
                            .map { makeTask(selection, it) }
                            .getOr(TaskNone)
                }
                .getOr(TaskNone)
    }

    fun makeTask(selection: ISelection, ref: IMaterialRef): ITask {
        val newModel = model.modifyObjects(model.getSelectedObjectRefs(selection)) { _, obj ->
            obj.transformer.withMaterial(obj, ref)
        }
        return TaskUpdateModel(oldModel = model, newModel = newModel)
    }
}

class LoadMaterial : IUseCase {

    override val key: String = "material.view.load"

    @Inject lateinit var component: Component
    @Inject lateinit var projectManager: ProjectManager

    override fun createTask(): ITask {
        return component
                .asNullable()
                .map { it.metadata["ref"] }
                .flatMap { it as? IMaterialRef }
                .flatMapNullable { ref ->

                    TinyFileDialogs.tinyfd_openFileDialog(
                            "Import Texture",
                            "",
                            textureExtensions,
                            "PNG texture (*.png)",
                            false
                    ).asNullable()
                            .map { makeTask(it, ref) }

                }.getOr(TaskNone)
    }

    fun makeTask(path: String, ref: IMaterialRef): ITask {
        val archive = File(path)
        val material = TexturedMaterial(archive.nameWithoutExtension, archive.toResourcePath())
        return TaskUpdateMaterial(
                ref = ref,
                oldMaterial = projectManager.loadedMaterials[ref.materialIndex],
                newMaterial = material
        )
    }
}

class ImportMaterial : IUseCase {

    override val key: String = "material.view.import"

    @Inject lateinit var projectManager: ProjectManager

    override fun createTask(): ITask {
        val file = TinyFileDialogs.tinyfd_openFileDialog("Import Texture", "",
                textureExtensions, "PNG texture (*.png)", false)

        if (file != null) {
            val archive = File(file)
            val material = TexturedMaterial(archive.nameWithoutExtension, archive.toResourcePath())
            return TaskImportMaterial(material)
        }
        return TaskNone
    }
}

class SelectMaterial : IUseCase {

    override val key: String = "material.view.select"

    @Inject lateinit var component: Component

    override fun createTask(): ITask {
        return component.metadata["ref"]
                .asNullable()
                .flatMap { it as? IMaterialRef }
                .map { TaskUpdateSelectedMaterial(it) as ITask }
                .getOr(TaskNone)
    }
}