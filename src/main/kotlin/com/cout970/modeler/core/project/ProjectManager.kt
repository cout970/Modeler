package com.cout970.modeler.core.project

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.core.model.Model

/**
 * Created by cout970 on 2017/07/08.
 */

class ProjectManager {

    var project: Project = Project(Author("anonymous"), "unnamed")

    var model: IModel = Model.empty()
        private set

    private val materialList = mutableListOf<IMaterial>()
    val loadedMaterials: List<IMaterial> = materialList

    var clipboard: Pair<IModel, ISelection>? = null

    val modelChangeListeners: MutableList<(old: IModel, new: IModel) -> Unit> = mutableListOf()
    val materialChangeListeners: MutableList<(old: IMaterial?, new: IMaterial?) -> Unit> = mutableListOf()

    fun loadMaterial(material: IMaterial) {
        if (material !in materialList) {
            materialList.add(material)
            materialChangeListeners.forEach { it.invoke(null, material) }
        }
    }

    fun updateMaterial(index: Int, new: IMaterial) {
        if (index in materialList.indices) {
            materialChangeListeners.forEach { it.invoke(materialList[index], new) }
            materialList[index] = new
        }
    }

    fun removeMaterial(material: IMaterial) {
        if (material in loadedMaterials) {
            materialList.remove(material)
            materialChangeListeners.forEach { it.invoke(material, null) }
        }
    }

    fun updateModel(model: IModel) {
        val old = this.model
        this.model = model
        modelChangeListeners.forEach { it.invoke(old, model) }
    }

    fun loadProject(new: Project) {
        project = new
    }
}