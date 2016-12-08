package com.cout970.modeler.modelcontrol.action

import com.cout970.modeler.model.*
import com.cout970.modeler.modelcontrol.ModelController
import com.cout970.modeler.modelcontrol.selection.Selection
import com.cout970.modeler.modelcontrol.selection.SelectionMode

/**
 * Created by cout970 on 2016/12/08.
 */
data class ActionDelete(val selection: Selection, val modelController: ModelController) : IAction {

    val model = modelController.model.copy()

    override fun run() {
        modelController.model.apply {
            when (selection.mode) {
                SelectionMode.GROUP -> objects.forEach { obj ->
                    obj.groups.removeAll { group -> selection.paths.any { it.group == group } }
                }
                SelectionMode.COMPONENT -> objects.forEach { obj ->
                    obj.groups.forEach { group ->
                        group.components.removeAll { component -> selection.paths.any { it.component == component } }
                    }
                }
                SelectionMode.QUAD -> objects.forEach { obj ->
                    obj.groups.forEach { group ->
                        val list = mutableListOf<ModelComponent>()
                        group.components.forEach { comp ->
                            if (selection.paths.any { it.component == comp }) {
                                when (comp) {
                                    is Plane -> Unit
                                    is Cube, is Mesh -> {
                                        val selectedQuads = selection.paths.map { it.quad }
                                        val unselectedQuads = comp.getQuads().filter { it !in selectedQuads }
                                        if (unselectedQuads.isNotEmpty()) {
                                            val vertex = unselectedQuads.flatMap(Quad::vertex).distinct()
                                            val indices = unselectedQuads.map {
                                                Mesh.QuadIndices(
                                                        vertex.indexOf(it.a), vertex.indexOf(it.b),
                                                        vertex.indexOf(it.c), vertex.indexOf(it.d))
                                            }
                                            list += Mesh(vertex, indices)
                                        }
                                    }
                                }
                            } else {
                                list += comp
                            }
                        }
                        group.components.clear()
                        group.components.addAll(list)

                    }
                }
                SelectionMode.VERTEX -> Unit //you cant remove vertex because everything needs to be made of quads
            }
            modelController.selectionManager.clearSelection()
            modelController.modelUpdate = true
        }
    }

    override fun undo() {
        modelController.model = model
        modelController.selectionManager.selection = selection
        modelController.modelUpdate = true
    }
}