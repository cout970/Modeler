package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IObjectCube
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.TaskNone
import com.cout970.modeler.controller.tasks.TaskUpdateModel
import com.cout970.modeler.core.model.`object`.ObjectCube
import com.cout970.modeler.core.model.getSelectedObjects
import com.cout970.modeler.core.model.objects
import com.cout970.modeler.core.project.IProgramState
import com.cout970.vector.extensions.vec2Of
import org.liquidengine.legui.component.Component

/**
 * Created by cout970 on 2017/07/20.
 */

@UseCase("update.template.cube")
private fun changeCube(comp: Component, access: IProgramState): ITask {
    val ref = getObjectRef(access) ?: return TaskNone
    val offset = comp.metadata["offset"] as? Float ?: return TaskNone
    val cmd = comp.metadata["command"] as? String ?: return TaskNone
    val text = comp.metadata["content"] as? String ?: return TaskNone

    val model = access.model
    val cube = model.getObject(ref) as? IObjectCube ?: return TaskNone
    val newObject = updateCube(cube, cmd, text, offset) ?: return TaskNone
    val newModel = model.modifyObjects(setOf(ref)) { _, _ -> newObject }

    return TaskUpdateModel(model, newModel)
}

private fun getObjectRef(access: IProgramState): IObjectRef? {
    val sel = access.modelSelection.getOrNull() ?: return null

    return if (isSelectingOneCube(access.model, sel)) sel.objects.first() else return null
}

private fun isSelectingOneCube(model: IModel, new: ISelection): Boolean {
    if (new.selectionType != SelectionType.OBJECT) return false
    if (new.selectionTarget != SelectionTarget.MODEL) return false
    if (new.size != 1) return false
    val selectedObj = model.getSelectedObjects(new).firstOrNull() ?: return false
    return selectedObj is ObjectCube
}

private fun updateCube(cube: IObjectCube, cmd: String, input: String, offset: Float): IObjectCube? {
    val obj: IObjectCube = when (cmd) {
        "tex.x" -> setTextureOffsetX(cube, x = getValue(input, cube.textureOffset.xf) + offset)
        "tex.y" -> setTextureOffsetY(cube, y = getValue(input, cube.textureOffset.yf) + offset)
        "tex.scale" -> setTextureSize(cube, getValue(input, cube.textureSize.xf) + offset)
        else -> {
            val t = updateTransformation(cube.transformation, cmd, input, offset) ?: return null
            cube.withTransformation(t)
        }
    }
    if (cube.transformation == obj.transformation && cube.textureOffset == obj.textureOffset && cube.textureSize == obj.textureSize) {
        return null
    }

    return obj
}

private fun setTextureOffsetX(cube: IObjectCube, x: Float): IObjectCube {
    return cube.withTextureOffset(vec2Of(x, cube.textureOffset.yf))
}

private fun setTextureOffsetY(cube: IObjectCube, y: Float): IObjectCube {
    return cube.withTextureOffset(vec2Of(cube.textureOffset.xf, y))
}

private fun setTextureSize(cube: IObjectCube, s: Float): IObjectCube {
    return cube.withTextureSize(vec2Of(s))
}