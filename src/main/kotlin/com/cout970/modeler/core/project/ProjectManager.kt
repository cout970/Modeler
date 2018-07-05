package com.cout970.modeler.core.project

import com.cout970.modeler.api.animation.IAnimation
import com.cout970.modeler.api.animation.IAnimationRef
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.core.animation.AnimationRefNone
import com.cout970.modeler.core.animation.animationOf
import com.cout970.modeler.core.animation.ref
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.model.Model
import com.cout970.modeler.core.model.material.MaterialNone
import com.cout970.modeler.core.model.material.MaterialRefNone
import com.cout970.modeler.core.model.material.TexturedMaterial
import com.cout970.modeler.core.model.ref
import com.cout970.modeler.core.model.selection.ClipboardNone
import com.cout970.modeler.core.model.selection.IClipboard
import com.cout970.modeler.core.model.selection.SelectionHandler

/**
 * Created by cout970 on 2017/07/08.
 */

class ProjectManager(
        val modelSelectionHandler: SelectionHandler,
        val textureSelectionHandler: SelectionHandler
) {

    var projectProperties: ProjectProperties = ProjectProperties(Config.user, "unnamed")

    var model: IModel = Model.empty()
        private set

    var clipboard: IClipboard = ClipboardNone

    var selectedMaterial: IMaterialRef = MaterialRefNone
    val material: IMaterial get() = model.materialMap[selectedMaterial] ?: MaterialNone

    var selectedAnimation: IAnimationRef = AnimationRefNone
    val animation: IAnimation get() = model.animationMap[selectedAnimation] ?: animationOf()

    val modelChangeListeners: MutableList<(old: IModel, new: IModel) -> Unit> = mutableListOf()

    val materialChangeListeners: MutableList<(old: IMaterial?, new: IMaterial?) -> Unit> = mutableListOf()

    val materialPaths get() = loadedMaterials.values.filterIsInstance<TexturedMaterial>()
    val loadedMaterials: Map<IMaterialRef, IMaterial> get() = model.materialMap

    fun loadMaterial(material: IMaterial) {
        if (material.ref !in loadedMaterials) {
            model = model.addMaterial(material)
            materialChangeListeners.forEach { it.invoke(null, material) }
        }
    }

    fun updateMaterial(ref: IMaterialRef, new: IMaterial) {
        if (ref in loadedMaterials) {
            materialChangeListeners.forEach { it.invoke(model.getMaterial(ref), new) }
            model = model.modifyMaterial(ref, new)
        }
    }

    fun removeMaterial(ref: IMaterialRef) {
        if (ref in loadedMaterials) {
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

    @Deprecated("Use model mutating tasks instead")
    fun updateAnimation(newAnimation: IAnimation) {
        model = model.modifyAnimation(newAnimation.ref, newAnimation)
    }

    fun loadProjectProperties(aNew: ProjectProperties) {
        projectProperties = aNew
    }
}