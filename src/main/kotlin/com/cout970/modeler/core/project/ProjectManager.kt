package com.cout970.modeler.core.project

import com.cout970.modeler.api.animation.IAnimation
import com.cout970.modeler.api.animation.IAnimationRef
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IGroupRef
import com.cout970.modeler.api.model.`object`.RootGroupRef
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.core.animation.AnimationRefNone
import com.cout970.modeler.core.animation.animationOf
import com.cout970.modeler.core.animation.ref
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.export.ExportManager
import com.cout970.modeler.core.export.ProgramSave
import com.cout970.modeler.core.model.Model
import com.cout970.modeler.core.model.material.MaterialNone
import com.cout970.modeler.core.model.material.MaterialRefNone
import com.cout970.modeler.core.model.material.TexturedMaterial
import com.cout970.modeler.core.model.ref
import com.cout970.modeler.core.model.selection.ClipboardNone
import com.cout970.modeler.core.model.selection.IClipboard
import com.cout970.modeler.core.model.selection.SelectionHandler
import com.cout970.modeler.util.Nullable

/**
 * Created by cout970 on 2017/07/08.
 */

class ProjectManager(
        override val modelSelectionHandler: SelectionHandler,
        override val textureSelectionHandler: SelectionHandler
) : IProgramState {

    override val modelSelection: Nullable<ISelection> get() = modelSelectionHandler.getSelection()
    override val textureSelection: Nullable<ISelection> get() = textureSelectionHandler.getSelection()

    var projectProperties: ProjectProperties = ProjectProperties(Config.user, "unnamed")

    override var model: IModel = Model.empty()

    override var selectedGroup: IGroupRef = RootGroupRef

    override var selectedMaterial: IMaterialRef = MaterialRefNone
    override val material: IMaterial get() = model.materialMap[selectedMaterial] ?: MaterialNone

    override var selectedAnimation: IAnimationRef = AnimationRefNone
    override val animation: IAnimation get() = model.animationMap[selectedAnimation] ?: animationOf()

    val modelChangeListeners: MutableList<(old: IModel, new: IModel) -> Unit> = mutableListOf()

    val materialChangeListeners: MutableList<(old: IMaterial?, new: IMaterial?) -> Unit> = mutableListOf()

    val materialPaths get() = loadedMaterials.values.filterIsInstance<TexturedMaterial>()
    val loadedMaterials: Map<IMaterialRef, IMaterial> get() = model.materialMap

    var clipboard: IClipboard = ClipboardNone

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
        updateModel(model.modifyAnimation(newAnimation.ref, newAnimation))
    }

    fun loadProjectProperties(aNew: ProjectProperties) {
        projectProperties = aNew
    }

    fun toProgramSave(saveImages: Boolean) = ProgramSave(
            ExportManager.CURRENT_SAVE_VERSION,
            projectProperties,
            model,
            animation,
            if (saveImages) materialPaths else emptyList()
    )
}