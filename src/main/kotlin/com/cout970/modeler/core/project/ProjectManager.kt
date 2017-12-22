package com.cout970.modeler.core.project

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.core.animation.Animation
import com.cout970.modeler.core.animation.Joint
import com.cout970.modeler.core.animation.KeyFrame
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.model.Model
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.core.model.selection.ClipboardNone
import com.cout970.modeler.core.model.selection.IClipboard
import com.cout970.modeler.core.model.selection.SelectionHandler

/**
 * Created by cout970 on 2017/07/08.
 */

class ProjectManager(val modelSelectionHandler: SelectionHandler, val textureSelectionHandler: SelectionHandler) {

    var projectProperties: ProjectProperties = ProjectProperties(Config.user, "unnamed")

    var model: IModel = Model.empty()
        private set

    val loadedMaterials: List<IMaterial> get() = model.materials

    var clipboard: IClipboard = ClipboardNone

    var animation = Animation(listOf(
            KeyFrame(0f, mapOf()),
            KeyFrame(10f, mapOf())
    ), Joint(0, "root", listOf(), TRSTransformation.IDENTITY))

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