package com.cout970.modeler.core.project

import com.cout970.modeler.api.animation.IAnimation
import com.cout970.modeler.api.animation.IAnimationRef
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.core.model.selection.SelectionHandler
import com.cout970.modeler.util.Nullable

/**
 * Created by cout970 on 2017/09/27.
 */
interface IProgramState {

    val model: IModel
    val material: IMaterial
    val animation: IAnimation

    val selectedMaterial: IMaterialRef
    val selectedAnimation: IAnimationRef

    val modelSelectionHandler: SelectionHandler
    val textureSelectionHandler: SelectionHandler

    val modelSelection: Nullable<ISelection> get() = modelSelectionHandler.getSelection()
    val textureSelection: Nullable<ISelection> get() = textureSelectionHandler.getSelection()

    operator fun component1() = model
    operator fun component2() = modelSelection
    operator fun component3() = textureSelection
}