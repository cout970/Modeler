package com.cout970.modeler.functional.usecases

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.core.model.getSelectedObjectRefs
import com.cout970.modeler.core.model.material.TexturedMaterial
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.resource.toResourcePath
import com.cout970.modeler.functional.injection.Inject
import com.cout970.modeler.functional.tasks.*
import com.cout970.modeler.util.parent
import com.cout970.modeler.view.gui.editor.rightpanel.RightPanel
import org.funktionale.option.Option
import org.funktionale.option.getOrElse
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
    @Inject lateinit var selection: Option<ISelection>

    override fun createTask(): ITask {
        return selection.map { selection ->
            component.parent<RightPanel.MaterialListItem>()?.let { item ->
                val newModel = model.modifyObjects(model.getSelectedObjectRefs(selection)) { _, obj ->
                    obj.transformer.withMaterial(obj, item.ref)
                }
                TaskUpdateModel(oldModel = model, newModel = newModel)
            } ?: TaskNone
        }.getOrElse { TaskNone }
    }
}

class LoadMaterial : IUseCase {

    override val key: String = "material.view.load"

    @Inject lateinit var component: Component
    @Inject lateinit var projectManager: ProjectManager

    override fun createTask(): ITask {
        component.parent<RightPanel.MaterialListItem>()?.let { item ->
            val file = TinyFileDialogs.tinyfd_openFileDialog("Import Texture", "",
                    textureExtensions, "PNG texture (*.png)", false)

            if (file != null) {
                val archive = File(file)
                val material = TexturedMaterial(archive.nameWithoutExtension, archive.toResourcePath())
                return TaskUpdateMaterial(item.ref, projectManager.loadedMaterials[item.ref.materialIndex], material)
            }
        }
        return TaskNone
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
        component.parent<RightPanel.MaterialListItem>()?.let { item ->
            return TaskUpdateSelectedMaterial(item.ref)
        }
        return TaskNone
    }
}