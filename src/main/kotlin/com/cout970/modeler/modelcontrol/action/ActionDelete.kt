package com.cout970.modeler.modelcontrol.action

import com.cout970.modeler.model.Mesh
import com.cout970.modeler.model.Quad
import com.cout970.modeler.model.QuadIndices
import com.cout970.modeler.modelcontrol.ModelController
import com.cout970.modeler.modelcontrol.selection.ModelPath
import com.cout970.modeler.modelcontrol.selection.Selection
import com.cout970.modeler.modelcontrol.selection.SelectionMode

/**
 * Created by cout970 on 2016/12/08.
 */
data class ActionDelete(val selection: Selection, val modelController: ModelController) : IAction {

    val model = modelController.model

    override fun run() {
        modelController.apply {
            when (selection.mode) {
                SelectionMode.GROUP -> {
                    updateModel(model.copy(model.objects.map { obj ->
                        obj.copy(obj.groups.filterNot { group ->
                            selection.isSelected(ModelPath.of(model, obj, group))
                        })
                    }))
                }
                SelectionMode.COMPONENT -> {
                    updateModel(model.copy(model.objects.map { obj ->
                        obj.copy(obj.groups.map { group ->
                            group.copy(group.meshes.filterNot { component ->
                                selection.isSelected(ModelPath.of(model, obj, group, component))
                            })
                        })
                    }))
                }
                SelectionMode.QUAD -> {
                    val selectedQuads = selection.paths.map { it.getQuad(model) }
                    updateModel(model.copy(model.objects.map { obj ->
                        obj.copy(obj.groups.map { group ->
                            group.copy(group.meshes.map { comp ->
                                if (selection.containsSelectedElements(ModelPath.of(model, obj, group, comp))) {

                                    val unselectedQuads = comp.getQuads().filter { it !in selectedQuads }
                                    if (unselectedQuads.isNotEmpty()) {
                                        val positions = unselectedQuads.flatMap(Quad::vertex).map { it.pos }.distinct()
                                        val textures = unselectedQuads.flatMap(Quad::vertex).map { it.tex }.distinct()
                                        val indices = unselectedQuads.map {
                                            QuadIndices(
                                                    positions.indexOf(it.a.pos), textures.indexOf(it.a.tex),
                                                    positions.indexOf(it.b.pos), textures.indexOf(it.b.tex),
                                                    positions.indexOf(it.c.pos), textures.indexOf(it.c.tex),
                                                    positions.indexOf(it.d.pos), textures.indexOf(it.d.tex))
                                        }
                                        //return a new mesh, if only some parts of the component are removed, but not all
                                        Mesh(positions, textures, indices)
                                    } else {
                                        //the mesh without the selected parts is empty
                                        null
                                    }
                                } else {
                                    //if not selected
                                    comp
                                }
                            }.filterNotNull())
                        })
                    }))
                }
                SelectionMode.VERTEX -> Unit //you can't remove a vertex because everything needs to be made of quads
            }
        }
        modelController.selectionManager.clearSelection()
    }

    override fun undo() {
        modelController.updateModel(model)
        modelController.selectionManager.selection = selection
    }

    override fun toString(): String {
        return "ActionDelete(selection=$selection, oldModel=$model)"
    }


}