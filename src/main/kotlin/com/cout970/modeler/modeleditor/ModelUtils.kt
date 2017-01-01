package com.cout970.modeler.modeleditor

import com.cout970.modeler.model.Model
import com.cout970.modeler.model.Quad
import com.cout970.modeler.modeleditor.selection.Selection
import com.cout970.modeler.modeleditor.selection.SelectionMode
import com.cout970.modeler.util.replace
import com.cout970.modeler.util.replaceSelected
import com.cout970.modeler.view.controller.SelectionAxis
import com.cout970.vector.extensions.plus
import com.cout970.vector.extensions.times

/**
 * Created by cout970 on 2016/12/17.
 */

fun Model.translate(selection: Selection, axis: SelectionAxis, offset: Float): Model {
    return when (selection.mode) {

        SelectionMode.GROUP -> {
            copy(objects.replaceSelected(selection) { objIndex, obj ->
                obj.copy(obj.groups.replaceSelected(selection, objIndex) { groupIndex, group ->
                    group.copy(transform = group.transform.move(axis, offset))
                })
            })
        }

        SelectionMode.MESH -> {
            copy(objects.replaceSelected(selection) { objIndex, obj ->
                obj.copy(obj.groups.replaceSelected(selection, objIndex) { groupIndex, group ->
                    group.copy(group.meshes.replaceSelected(selection, objIndex, groupIndex) { meshIndex, mesh ->
                        mesh.copy(transform = mesh.transform.move(axis, offset))
                    })
                })
            })
        }
        SelectionMode.QUAD -> {
            copy(objects.replaceSelected(selection) { objIndex, obj ->
                obj.copy(obj.groups.replaceSelected(selection, objIndex) { groupIndex, group ->
                    group.copy(group.meshes.replaceSelected(selection, objIndex, groupIndex) { meshIndex, mesh ->

                        val selectedQuadsIndex = selection.paths.filter { it.getMesh(this) == mesh }.map { it.quad }
                        val selectedPositions = mesh.indices
                                .filterIndexed { index, _ -> index in selectedQuadsIndex }
                                .map { it.toQuad(mesh.positions, mesh.textures) }
                                .flatMap(Quad::vertex)
                                .map { it.pos }
                                .distinct()

                        mesh.copy(positions = mesh.positions.replace({ pos -> pos in selectedPositions }, { pos ->
                            pos + axis.axis * offset
                        }))
                    })
                })
            })
        }

        SelectionMode.VERTEX -> {
            copy(objects.replaceSelected(selection) { objIndex, obj ->
                obj.copy(obj.groups.replaceSelected(selection, objIndex) { groupIndex, group ->
                    group.copy(group.meshes.replaceSelected(selection, objIndex, groupIndex) { meshIndex, mesh ->

                        val pathToThisComponent = selection.paths.filter { it.getMesh(this) == mesh }
                        val selectedIndices = pathToThisComponent.map { it.vertex }

                        mesh.copy(positions = mesh.positions.mapIndexed { i, pos ->
                            if (i in selectedIndices) {
                                pos + axis.axis * offset
                            } else {
                                pos
                            }
                        })
                    })
                })
            })
        }
        else -> this
    }
}