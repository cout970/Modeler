package com.cout970.modeler.modeleditor

import com.cout970.modeler.model.Model
import com.cout970.modeler.model.Quad
import com.cout970.modeler.modeleditor.selection.IModelSelection
import com.cout970.modeler.modeleditor.selection.ModelSelectionMode
import com.cout970.modeler.util.*
import com.cout970.modeler.view.controller.SelectionAxis
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
import org.joml.Quaterniond

/**
 * Created by cout970 on 2016/12/17.
 */

fun Model.translate(selection: IModelSelection, axis: SelectionAxis, offset: Float): Model {
    return when (selection.modelMode) {

        ModelSelectionMode.GROUP -> {
            applyGroup(selection) { group ->
                group.copy(transform = group.transform.translate(axis, offset))
            }
        }

        ModelSelectionMode.MESH -> {
            applyMesh(selection) { mesh ->
                mesh.translate(axis.direction * offset)
            }
        }
        ModelSelectionMode.QUAD -> {
            applyMeshAndGroup(selection) { mesh, group ->
                val selectedQuadsIndex = selection.paths.filter { it.getMesh(this) == mesh }.map { it.quad }
                val selectedPositions = mesh.indices
                        .filterIndexed { index, _ -> index in selectedQuadsIndex }
                        .map { it.toQuad(mesh.positions, mesh.textures) }
                        .flatMap(Quad::vertex)
                        .map { it.pos }
                        .distinct()

                val newMesh = mesh.copy(positions = mesh.positions.replace({ pos -> pos in selectedPositions }, { pos ->
                    pos + axis.direction * offset
                }))
                if (newMesh.isCuboid()) {
                    val size = newMesh.getCuboidSize()
                    newMesh.setUVFromCuboid(size, vec2Of(0, 0), group.material.size)
                } else {
                    newMesh
                }
            }
        }

        ModelSelectionMode.VERTEX -> {
            applyMesh(selection) { mesh ->
                val pathToThisComponent = selection.paths.filter { it.getMesh(this) == mesh }
                val selectedIndices = pathToThisComponent.map { mesh.indices[it.quad].positions[it.vertex] }

                mesh.copy(positions = mesh.positions.mapIndexed { i, pos ->
                    if (i in selectedIndices) {
                        pos + axis.direction * offset
                    } else {
                        pos
                    }
                })
            }
        }
        else -> this
    }
}

fun Model.rotate(selection: IModelSelection, axis: SelectionAxis, offset: Float): Model {
    val axisDir = when (axis) {
        SelectionAxis.X -> SelectionAxis.Y
        SelectionAxis.Y -> SelectionAxis.Z
        SelectionAxis.Z -> SelectionAxis.X
        else -> SelectionAxis.NONE
    }
    return when (selection.modelMode) {

        ModelSelectionMode.GROUP -> {
            applyGroup(selection) { group ->
                group.copy(transform = group.transform.rotate(axisDir, offset))
            }
        }

        ModelSelectionMode.MESH -> {
            val center = selection.getCenter3D(this)
            applyMesh(selection) { mesh ->
                mesh.copy(positions = mesh.positions.map { pos ->
                    rotatePointAroundPivot(pos, center, axisDir.direction * offset)
                })
            }
        }
        ModelSelectionMode.QUAD -> {
            val center = selection.getCenter3D(this)
            applyMesh(selection) { mesh ->
                val selectedQuadsIndex = selection.paths.filter { it.getMesh(this) == mesh }.map { it.quad }
                val selectedPositions = mesh.indices
                        .filterIndexed { index, _ -> index in selectedQuadsIndex }
                        .map { it.toQuad(mesh.positions, mesh.textures) }
                        .flatMap(Quad::vertex)
                        .map { it.pos }
                        .distinct()

                mesh.copy(positions = mesh.positions.replace({ pos -> pos in selectedPositions }, { pos ->
                    rotatePointAroundPivot(pos, center, axisDir.direction * offset)
                }))
            }
        }

        ModelSelectionMode.VERTEX -> {
            val center = selection.getCenter3D(this)
            applyMesh(selection) { mesh ->
                val pathToThisComponent = selection.paths.filter { it.getMesh(this) == mesh }
                val selectedIndices = pathToThisComponent.map { mesh.indices[it.quad].positions[it.vertex] }

                mesh.copy(positions = mesh.positions.mapIndexed { i, pos ->
                    if (i in selectedIndices) {
                        rotatePointAroundPivot(pos, center, axisDir.direction * offset)
                    } else {
                        pos
                    }
                })
            }
        }
        else -> this
    }
}

fun Model.scale(selection: IModelSelection, axis: SelectionAxis, offset: Float): Model {
    return when (selection.modelMode) {

        ModelSelectionMode.GROUP -> {
            applyGroup(selection) { group ->
                group.copy(transform = group.transform.scale(axis, offset))
            }
        }

        ModelSelectionMode.MESH -> {
            val center = selection.getCenter3D(this)
            applyMesh(selection) { mesh ->
                mesh.scale(center, axis, offset)
            }
        }

        ModelSelectionMode.QUAD -> {
            val center = selection.getCenter3D(this)
            applyMeshAndGroup(selection) { mesh, group ->
                val selectedQuadsIndex = selection.paths.filter { it.getMesh(this) == mesh }.map { it.quad }
                val selectedPositions = mesh.indices
                        .filterIndexed { index, _ -> index in selectedQuadsIndex }
                        .map { it.toQuad(mesh.positions, mesh.textures) }
                        .flatMap(Quad::vertex)
                        .map { it.pos }
                        .distinct()

                val newMesh = mesh.copy(positions = mesh.positions.replace({ pos -> pos in selectedPositions }, { pos ->
                    pos.scale(center, axis, offset)
                }))
                if (newMesh.isCuboid()) {
                    val size = newMesh.getCuboidSize()
                    newMesh.setUVFromCuboid(size, vec2Of(0, 0), group.material.size)
                } else {
                    newMesh
                }
            }
        }

        ModelSelectionMode.VERTEX -> {
            if (selection.paths.size <= 1) {
                this
            } else {
                val center = selection.getCenter3D(this)
                applyMesh(selection) { mesh ->
                    val pathToThisComponent = selection.paths.filter { it.getMesh(this) == mesh }
                    val selectedIndices = pathToThisComponent.map { mesh.indices[it.quad].positions[it.vertex] }

                    mesh.copy(positions = mesh.positions.mapIndexed { i, pos ->
                        if (i in selectedIndices) {
                            pos.scale(center, axis, offset)
                        } else {
                            pos
                        }
                    })
                }
            }
        }
        else -> this
    }
}

fun rotatePointAroundPivot(point: IVector3, pivot: IVector3, angles: IVector3): IVector3 {
    var dir: IVector3 = point - pivot // get point direction relative to pivot
    dir = Quaterniond().rotateXYZ(angles.xd, angles.yd, angles.zd).transform(dir.toJoml3d()).toIVector() // rotate it
    return dir + pivot // calculate rotated point
}
