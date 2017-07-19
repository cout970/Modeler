package com.cout970.modeler.core.project

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.core.model.Model

/**
 * Created by cout970 on 2017/07/08.
 */

class ProjectManager {

    var projectProperties: ProjectProperties = ProjectProperties(Author("anonymous"), "unnamed")

    var model: IModel = Model.empty()
        private set

    val loadedMaterials: List<IMaterial> get() = model.materials

    var clipboard: Pair<IModel, ISelection>? = null

    val modelChangeListeners: MutableList<(old: IModel, new: IModel) -> Unit> = mutableListOf()
    val materialChangeListeners: MutableList<(old: IMaterial?, new: IMaterial?) -> Unit> = mutableListOf()

    fun loadMaterial(material: IMaterial) {
        if (material !in loadedMaterials) {
            model = model.addMaterial(material)
            materialChangeListeners.forEach { it.invoke(null, material) }
        }
    }

    fun updateMaterial(ref: IMaterialRef, new: IMaterial) {
        if (ref.materialIndex in loadedMaterials.indices) {
            materialChangeListeners.forEach { it.invoke(model.getMaterial(ref), new) }
            model = model.modifyMaterial(ref, new)
        }
    }

    fun removeMaterial(ref: IMaterialRef) {
        if (ref.materialIndex in loadedMaterials.indices) {
            val oldMaterial = model.getMaterial(ref)
            model = model.removeMaterial(ref)
            materialChangeListeners.forEach { it.invoke(oldMaterial, null) }
        }
    }

    fun updateModel(model: IModel) {
        val old = this.model
        this.model = model
        modelChangeListeners.forEach { it.invoke(old, model) }
    }

    fun loadProjectProperties(aNew: ProjectProperties) {
        projectProperties = aNew
    }
}