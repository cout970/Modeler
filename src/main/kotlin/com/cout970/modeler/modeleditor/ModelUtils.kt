package com.cout970.modeler.modeleditor

import com.cout970.modeler.model.Mesh
import com.cout970.modeler.model.Model
import com.cout970.modeler.model.ModelGroup
import com.cout970.modeler.model.Quad
import com.cout970.modeler.modeleditor.selection.Selection
import com.cout970.modeler.modeleditor.selection.SelectionMode
import com.cout970.modeler.util.replace
import com.cout970.modeler.util.replaceSelected
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.util.toJoml3d
import com.cout970.modeler.view.controller.SelectionAxis
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
import org.joml.Quaterniond

/**
 * Created by cout970 on 2016/12/17.
 */

fun Model.translate(selection: Selection, axis: SelectionAxis, offset: Float): Model {
    return when (selection.mode) {

        SelectionMode.GROUP -> {
            applyGroup(selection) { group ->
                group.copy(transform = group.transform.move(axis, offset))
            }
        }

        SelectionMode.MESH -> {
            applyMesh(selection) { mesh ->
                mesh.translate(axis.axis * offset)
            }
        }
        SelectionMode.QUAD -> {
            applyMesh(selection) { mesh ->
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
            }
        }

        SelectionMode.VERTEX -> {
            applyMesh(selection) { mesh ->
                val pathToThisComponent = selection.paths.filter { it.getMesh(this) == mesh }
                val selectedIndices = pathToThisComponent.map { it.vertex }

                mesh.copy(positions = mesh.positions.mapIndexed { i, pos ->
                    if (i in selectedIndices) {
                        pos + axis.axis * offset
                    } else {
                        pos
                    }
                })
            }
        }
        else -> this
    }
}

fun Model.rotate(selection: Selection, axis: SelectionAxis, offset: Float): Model {
    return when (selection.mode) {

        SelectionMode.GROUP -> {
            applyGroup(selection) { group ->
                group.copy(transform = group.transform.rotate(axis, offset))
            }
        }

        SelectionMode.MESH -> {
            val center = selection.getCenter(this)
            applyMesh(selection) { mesh ->
                mesh.copy(positions = mesh.positions.map { pos ->
                    rotatePointAroundPivot(pos, center, axis.axis * offset)
                })
            }
        }
        SelectionMode.QUAD -> {
            val center = selection.getCenter(this)
            applyMesh(selection) { mesh ->
                val selectedQuadsIndex = selection.paths.filter { it.getMesh(this) == mesh }.map { it.quad }
                val selectedPositions = mesh.indices
                        .filterIndexed { index, _ -> index in selectedQuadsIndex }
                        .map { it.toQuad(mesh.positions, mesh.textures) }
                        .flatMap(Quad::vertex)
                        .map { it.pos }
                        .distinct()

                mesh.copy(positions = mesh.positions.replace({ pos -> pos in selectedPositions }, { pos ->
                    rotatePointAroundPivot(pos, center, axis.axis * offset)
                }))
            }
        }

        SelectionMode.VERTEX -> {
            val center = selection.getCenter(this)
            applyMesh(selection) { mesh ->
                val pathToThisComponent = selection.paths.filter { it.getMesh(this) == mesh }
                val selectedIndices = pathToThisComponent.map { it.vertex }

                mesh.copy(positions = mesh.positions.mapIndexed { i, pos ->
                    if (i in selectedIndices) {
                        rotatePointAroundPivot(pos, center, axis.axis * offset)
                    } else {
                        pos
                    }
                })
            }
        }
        else -> this
    }
}

fun Model.applyGroup(selection: Selection, groupFunc: (ModelGroup) -> ModelGroup): Model {
    return copy(objects.replaceSelected(selection) { objIndex, obj ->
        obj.copy(obj.groups.replaceSelected(selection, objIndex) { _, group ->
            groupFunc(group)
        })
    })
}

fun Model.applyMesh(selection: Selection, meshFunc: (Mesh) -> Mesh): Model {
    return copy(objects.replaceSelected(selection) { objIndex, obj ->
        obj.copy(obj.groups.replaceSelected(selection, objIndex) { groupIndex, group ->
            group.copy(group.meshes.replaceSelected(selection, objIndex, groupIndex) { _, mesh ->
                meshFunc(mesh)
            })
        })
    })
}

fun rotatePointAroundPivot(point: IVector3, pivot: IVector3, angles: IVector3): IVector3 {
    var dir: IVector3 = point - pivot // get point direction relative to pivot
    dir = Quaterniond().rotateXYZ(angles.xd, angles.yd, angles.zd).transform(dir.toJoml3d()).toIVector() // rotate it
    return dir + pivot // calculate rotated point
}

//fun updateTextures(): Mesh {
//    val mapIndexes = mutableMapOf<Int, Int>()
//    var index = 0
//    val textures = indices.flatMap { quad ->
//        val rayQuad = quad.toQuad(positions, textures)
//        val size = Math.max(rayQuad.a.pos distance rayQuad.b.pos, Math.max(rayQuad.b.pos distance rayQuad.c.pos,
//                Math.max(rayQuad.c.pos distance rayQuad.d.pos, rayQuad.d.pos distance rayQuad.a.pos)))
//
//        mapIndexes += quad.aT to index
//        mapIndexes += quad.bT to index + 1
//        mapIndexes += quad.cT to index + 2
//        mapIndexes += quad.dT to index + 3
//        index += 4
//        listOf(vec2Of(0, 0) * size,
//                vec2Of(1, 0) * size,
//                vec2Of(1, 1) * size,
//                vec2Of(0, 1) * size)
//
//    }
//    return Mesh(positions, textures, indices.map {
//        QuadIndices(
//                it.aP, mapIndexes[it.aT]!!,
//                it.bP, mapIndexes[it.bT]!!,
//                it.cP, mapIndexes[it.cT]!!,
//                it.dP, mapIndexes[it.dT]!!)
//    })
//}