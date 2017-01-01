package com.cout970.modeler.modeleditor.action

import com.cout970.modeler.model.Mesh
import com.cout970.modeler.model.Model
import com.cout970.modeler.modeleditor.ModelController
import com.cout970.modeler.modeleditor.selection.ModelPath
import com.cout970.modeler.modeleditor.selection.Selection
import com.cout970.modeler.modeleditor.selection.SelectionGroup
import com.cout970.modeler.modeleditor.selection.SelectionMode
import com.cout970.modeler.util.flatMapIndexed
import com.cout970.modeler.util.replaceSelected

/**
 * Created by cout970 on 2016/12/09.
 */
class ActionPaste(val selection: Selection, val copiedModel: Model, val modelController: ModelController) : IAction {

    //the model when the action is executed
    val model = modelController.model

    override fun run() {
        when (selection.mode) {
            SelectionMode.GROUP -> {
                val groups = copiedModel.objects.flatMapIndexed { objIndex, obj ->
                    obj.groups.filterIndexed { groupIndex, group ->
                        selection.isSelected(ModelPath(objIndex, groupIndex))
                    }
                }
                val insertSelection = SelectionGroup(listOf(modelController.inserter.insertPath))
                val newModel = model.copy(model.objects.replaceSelected(insertSelection, { i, obj ->
                    obj.addAll(groups)
                }))
                modelController.updateModel(newModel)
            }
            SelectionMode.MESH -> {
                val meshes = copiedModel.objects.flatMapIndexed { objIndex, obj ->
                    obj.groups.flatMapIndexed { groupIndex, group ->
                        group.meshes.filterIndexed { meshIndex, mesh ->
                            selection.isSelected(ModelPath(objIndex, groupIndex, meshIndex))
                        }
                    }
                }
                val insertSelection = SelectionGroup(listOf(modelController.inserter.insertPath))
                val newModel = model.copy(model.objects.replaceSelected(insertSelection) { objIndex, obj ->
                    obj.copy(obj.groups.replaceSelected(insertSelection, objIndex) { _, group ->
                        group.addAll(meshes)
                    })
                })
                modelController.updateModel(newModel)
            }
            SelectionMode.QUAD -> {
                val quadMeshPairs = copiedModel.objects.flatMapIndexed { objIndex, obj ->
                    obj.groups.flatMapIndexed { groupIndex, group ->
                        group.meshes.flatMapIndexed { meshIndex, mesh ->
                            mesh.getQuads().filterIndexed { quadIndex, quad ->
                                selection.isSelected(ModelPath(objIndex, groupIndex, meshIndex, quadIndex))
                            }.map { it to mesh }
                        }
                    }
                }
                val meshes = quadMeshPairs.map { (quad, mesh) -> Mesh.quadsToMesh(listOf(quad), mesh.transform) }

                val insertSelection = SelectionGroup(listOf(modelController.inserter.insertPath))
                val newModel = model.copy(model.objects.replaceSelected(insertSelection) { objIndex, obj ->
                    obj.copy(obj.groups.replaceSelected(insertSelection, objIndex) { _, group ->
                        group.addAll(meshes)
                    })
                })
                modelController.updateModel(newModel)
            }
            SelectionMode.VERTEX -> IllegalStateException("Trying to paste vertex")
        }
    }

    override fun undo() {
        modelController.updateModel(model)
    }

    override fun toString(): String {
        return "ActionPaste(selection=$selection, model_when_copied=$copiedModel, model_when_pasted=$model, modelController=$modelController)"
    }
}