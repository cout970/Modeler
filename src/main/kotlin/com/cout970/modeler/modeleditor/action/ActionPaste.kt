package com.cout970.modeler.modeleditor.action

import com.cout970.modeler.model.Mesh
import com.cout970.modeler.model.Model
import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.modeleditor.selection.IModelSelection
import com.cout970.modeler.modeleditor.selection.ModelPath
import com.cout970.modeler.modeleditor.selection.ModelSelectionMode
import com.cout970.modeler.modeleditor.selection.SelectionGroup
import com.cout970.modeler.util.flatMapIndexed
import com.cout970.modeler.util.replaceSelected

/**
 * Created by cout970 on 2016/12/09.
 */
class ActionPaste(val selection: IModelSelection, val copiedModel: Model, val modelEditor: ModelEditor) : IAction {

    //the model when the action is executed
    val model = modelEditor.model

    override fun run() {
        when (selection.modelMode) {
            ModelSelectionMode.GROUP -> {
                val groups = copiedModel.groups.flatMapIndexed { groupIndex, (groups) ->
                    groups.filterIndexed { groupIndex, _ ->
                        selection.isSelected(ModelPath(groupIndex, groupIndex))
                    }
                }
                val insertSelection = SelectionGroup(listOf(ModelPath(modelEditor.inserter.insertPath)))
                val newModel = model.copy(model.groups.replaceSelected(insertSelection, { _, group ->
                    group.addAll(groups)
                }))
                modelEditor.updateModel(newModel)
            }
            ModelSelectionMode.MESH -> {
                val meshes = copiedModel.groups.flatMapIndexed { groupIndex, group ->
                    group.meshes.filterIndexed { meshIndex, mesh ->
                        selection.isSelected(ModelPath(groupIndex, meshIndex))
                    }
                }
                val insertSelection = SelectionGroup(listOf(ModelPath(modelEditor.inserter.insertPath)))
                val newModel = model.copy(model.groups.replaceSelected(insertSelection) { _, group ->
                    group.addAll(meshes)
                })
                modelEditor.updateModel(newModel)
            }
            ModelSelectionMode.QUAD -> {
                val quadMeshPairs = copiedModel.groups.flatMapIndexed { groupIndex, group ->
                    group.meshes.flatMapIndexed { meshIndex, mesh ->
                        mesh.getQuads().filterIndexed { quadIndex, quad ->
                            selection.isSelected(ModelPath(groupIndex, meshIndex, quadIndex))
                        }
                    }
                }
                val meshes = quadMeshPairs.map { quad -> Mesh.quadsToMesh(listOf(quad)) }

                val insertSelection = SelectionGroup(listOf(ModelPath(modelEditor.inserter.insertPath)))
                val newModel = model.copy(model.groups.replaceSelected(insertSelection) { _, group ->
                    group.addAll(meshes)
                })
                modelEditor.updateModel(newModel)
            }
            ModelSelectionMode.VERTEX -> IllegalStateException("Trying to paste vertex")
        }
    }

    override fun undo() {
        modelEditor.updateModel(model)
    }

    override fun toString(): String {
        return "ActionPaste(selection=$selection, model_when_copied=$copiedModel, model_when_pasted=$model, modelController=$modelEditor)"
    }
}