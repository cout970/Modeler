package com.cout970.modeler.modelcontrol.action

import com.cout970.modeler.model.Mesh
import com.cout970.modeler.model.Model
import com.cout970.modeler.modelcontrol.ModelController
import com.cout970.modeler.modelcontrol.selection.ModelPath
import com.cout970.modeler.modelcontrol.selection.Selection
import com.cout970.modeler.modelcontrol.selection.SelectionMode
import com.cout970.modeler.util.replaceWithIndex

/**
 * Created by cout970 on 2016/12/09.
 */
class ActionPaste(val selection: Selection, val copiedModel: Model, val modelController: ModelController) : IAction {

    //the model when the action is executed
    val model = modelController.model

    override fun run() {
        when (selection.mode) {
            SelectionMode.GROUP -> {
                val groups = copiedModel.objects.flatMap { obj ->
                    obj.groups.filter { group ->
                        selection.isSelected(ModelPath.of(copiedModel, obj, group))
                    }
                }
                val objInd = modelController.inserter.objectIndex
                val newModel = model.copy(model.objects.replaceWithIndex({ i, v -> i == objInd }, { i, obj ->
                    var aux = obj
                    groups.forEach {
                        aux = aux.add(it)
                    }
                    aux
                }))
                modelController.updateModel(newModel)
            }
            SelectionMode.COMPONENT -> {
                val components = copiedModel.objects.flatMap { obj ->
                    obj.groups.flatMap { group ->
                        group.meshes.filter { selection.isSelected(ModelPath.of(copiedModel, obj, group, it)) }
                    }
                }
                val objInd = modelController.inserter.objectIndex
                val groupInd = modelController.inserter.groupIndex
                val newModel = model.copy(model.objects.replaceWithIndex({ i, v -> i == objInd }, { i, obj ->
                    obj.copy(obj.groups.replaceWithIndex({ i, v -> i == groupInd }, { i, group ->
                        var aux = group
                        components.forEach {
                            aux = aux.add(it)
                        }
                        aux
                    }))
                }))
                modelController.updateModel(newModel)
            }
            SelectionMode.QUAD -> {
                val components = copiedModel.objects.flatMap { obj ->
                    obj.groups.flatMap { group ->
                        group.meshes.flatMap { comp ->
                            comp.getQuads().filterIndexed { i, quad -> selection.isSelected(ModelPath.of(copiedModel, obj, group, comp, i)) }
                        }
                    }
                }.map { quad -> Mesh.quadsToMesh(listOf(quad)) }

                val objInd = modelController.inserter.objectIndex
                val groupInd = modelController.inserter.groupIndex
                val newModel = model.copy(model.objects.replaceWithIndex({ i, v -> i == objInd }, { i, obj ->
                    obj.copy(obj.groups.replaceWithIndex({ i, v -> i == groupInd }, { i, group ->
                        var aux = group
                        components.forEach {
                            aux = aux.add(it)
                        }
                        aux
                    }))
                }))
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