package com.cout970.modeler.modeleditor.action

import com.cout970.modeler.model.Mesh
import com.cout970.modeler.model.QuadIndices
import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.modeleditor.selection.ModelPath
import com.cout970.modeler.modeleditor.selection.Selection
import com.cout970.modeler.modeleditor.selection.SelectionMode
import com.cout970.modeler.util.filterNotIndexed
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2016/12/08.
 */
data class ActionDelete(val selection: Selection, val modelEditor: ModelEditor) : IAction {

    val model = modelEditor.model

    override fun run() {
        modelEditor.apply {
            when (selection.mode) {
                SelectionMode.GROUP -> {
                    updateModel(model.copy(model.groups.filterNotIndexed { groupIndex, group ->
                        selection.isSelected(ModelPath(groupIndex))
                    }))
                }
                SelectionMode.MESH -> {
                    updateModel(model.copy(model.groups.mapIndexed { groupIndex, group ->
                        group.copy(group.meshes.filterNotIndexed { meshIndex, mesh ->
                            selection.isSelected(ModelPath(groupIndex, meshIndex))
                        })
                    }))
                }
                SelectionMode.QUAD -> {

                    updateModel(model.copy(model.groups.mapIndexed { groupIndex, group ->
                        group.copy(group.meshes.mapIndexedNotNull { meshIndex, mesh ->
                            if (!selection.containsSelectedElements(ModelPath(groupIndex, meshIndex))) {
                                // unaffected mesh
                                mesh
                            } else {
                                val unselectedQuads = mesh.indices.filterNotIndexed { quadIndex, quadIndices ->
                                    selection.isSelected(ModelPath(groupIndex, meshIndex, quadIndex))
                                }
                                if (unselectedQuads.isNotEmpty()) {
                                    val posIndexMap = mutableMapOf<Int, Int>()
                                    val texIndexMap = mutableMapOf<Int, Int>()
                                    val positions = mutableListOf<IVector3>()
                                    val textureCoords = mutableListOf<IVector2>()
                                    for (quad in unselectedQuads) {
                                        for (pos in quad.positions) {
                                            posIndexMap += pos to positions.size
                                            positions.add(mesh.positions[pos])
                                        }
                                        for (tex in quad.textureCoords) {
                                            texIndexMap += tex to textureCoords.size
                                            textureCoords.add(mesh.textures[tex])
                                        }
                                    }
                                    val indices = unselectedQuads.map { (aP, aT, bP, bT, cP, cT, dP, dT) ->
                                        QuadIndices(
                                                posIndexMap[aP]!!, texIndexMap[aT]!!,
                                                posIndexMap[bP]!!, texIndexMap[bT]!!,
                                                posIndexMap[cP]!!, texIndexMap[cT]!!,
                                                posIndexMap[dP]!!, texIndexMap[dT]!!)
                                    }
                                    //return a new mesh, if only some parts of the component are removed, but not all
                                    Mesh(positions, textureCoords, indices)
                                } else {
                                    // the resulting mesh is empty
                                    null
                                }
                            }
                        })
                    }))
                }
                SelectionMode.VERTEX -> Unit //you can't remove a vertex because everything needs to be made of quads
            }
        }
        modelEditor.selectionManager.clearSelection()
    }

    override fun undo() {
        modelEditor.updateModel(model)
        modelEditor.selectionManager.selection = selection
    }

    override fun toString(): String {
        return "ActionDelete(selection=$selection, oldModel=$model)"
    }


}