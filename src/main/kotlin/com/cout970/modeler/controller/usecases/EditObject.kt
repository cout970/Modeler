package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.model.`object`.IGroupRef
import com.cout970.modeler.api.model.selection.*
import com.cout970.modeler.controller.tasks.*
import com.cout970.modeler.core.model.`object`.Object
import com.cout970.modeler.core.model.faces
import com.cout970.modeler.core.model.getSelectedObjects
import com.cout970.modeler.core.model.mesh.FaceIndex
import com.cout970.modeler.core.model.mesh.Mesh
import com.cout970.modeler.core.model.objects
import com.cout970.modeler.core.model.selection.Selection
import com.cout970.modeler.core.project.IProgramState
import com.cout970.modeler.render.tool.addFace
import com.cout970.modeler.render.tool.getEdges
import com.cout970.modeler.render.tool.getFacePos
import com.cout970.modeler.render.tool.removeFaces
import com.cout970.modeler.util.Nullable
import com.cout970.modeler.util.asNullable
import com.cout970.modeler.util.getOr
import com.cout970.modeler.util.text
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.*
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.TextInput
import java.util.*

/**
 * Created by cout970 on 2017/10/29.
 */

@UseCase("model.obj.change.name")
private fun changeObjectName(component: Component, programState: IProgramState): ITask {
    val model = programState.model
    val selection = programState.modelSelection

    val name = component.asNullable()
            .filterIsInstance<TextInput>()
            .map { it.text }
            .filter { !it.isBlank() }

    val objRef = selection
            .filter { it.size == 1 }
            .flatMap { it.refs.firstOrNull() }
            .filterIsInstance<IObjectRef>()

    return name.zip(objRef).map { (name, ref) ->

        val newModel = model.modifyObjects({ it == ref }) { _, obj ->
            obj.withName(name)
        }

        TaskUpdateModel(newModel = newModel, oldModel = model)
    }.getOr(TaskNone)
}

@UseCase("model.group.change.name")
private fun changeGroupName(component: Component, programState: IProgramState): ITask {
    val model = programState.model
    val groupRef = component.metadata["ref"] as IGroupRef

    val name = component.asNullable()
            .filterIsInstance<TextInput>()
            .map { it.text }
            .filter { !it.isBlank() }
            .getOrNull() ?: return TaskNone

    val group = model.getGroup(groupRef)
    val newModel = model.modifyGroup(groupRef, group = group.withName(name))

    return TaskUpdateModel(newModel = newModel, oldModel = model)
}


@UseCase("model.obj.join")
private fun joinObjects(programState: IProgramState): ITask {
    val selection = programState.modelSelection.getOrNull() ?: return TaskNone
    if (selection.selectionType != SelectionType.OBJECT || selection.size < 2) return TaskNone

    val model = programState.model
    val objs = model.getSelectedObjects(selection)
    val objsRefs = selection.objects
    val newObj = Object(
            name = objs.first().name,
            mesh = objs.map { it.mesh }.reduce { acc, mesh -> acc.merge(mesh) },
            material = objs.first().material
    )
    val newModel = model.removeObjects(objsRefs).addObjects(listOf(newObj))

    return TaskChain(listOf(
            TaskUpdateModelSelection(
                    oldSelection = programState.modelSelection,
                    newSelection = Nullable.castNull()
            ),
            TaskUpdateTextureSelection(
                    oldSelection = programState.textureSelection,
                    newSelection = Nullable.castNull()
            ),
            TaskUpdateModel(oldModel = model, newModel = newModel)
    ))
}

@UseCase("model.obj.arrange.uv")
private fun arrangeUVs(programState: IProgramState): ITask {
    val selection = programState.modelSelection.getOrNull() ?: return TaskNone
    if (selection.selectionType != SelectionType.OBJECT) return TaskNone

    val model = programState.model

    val newModel = model.modifyObjects(selection.objects.toSet()) { _, obj ->
        val mesh = obj.mesh
        val newTex = mutableListOf<IVector2>()

        val newFaces = mesh.faces.map {
            val a = mesh.pos[it.pos[0]]
            val b = mesh.pos[it.pos[1]]
            val c = mesh.pos[it.pos[2]]
            val d = mesh.pos[it.pos[3]]

            val ac = c - a
            val bd = d - b
            val normal = (ac cross bd).normalize()

            // 3d axis representing the 2d axis in the plane space
            val orthoX = (d - c).normalize()
            val orthoY = orthoX cross normal

            repeat(4) { index ->
                val point3d = mesh.pos[it.pos[index]]
                val relPoint = point3d - a

                val x = orthoX.dot(relPoint) * 1f / 16f
                val y = orthoY.dot(relPoint) * 1f / 16f

                newTex += vec2Of(x, y)
            }

            FaceIndex.from(it.pos, listOf(newTex.size - 4, newTex.size - 3, newTex.size - 2, newTex.size - 1))
        }

        obj.withMesh(Mesh(
                pos = mesh.pos,
                tex = newTex,
                faces = newFaces
        ))
    }

    return TaskChain(listOf(
            TaskUpdateModelSelection(
                    oldSelection = programState.modelSelection,
                    newSelection = Nullable.castNull()
            ),
            TaskUpdateTextureSelection(
                    oldSelection = programState.textureSelection,
                    newSelection = Nullable.castNull()
            ),
            TaskUpdateModel(oldModel = model, newModel = newModel)
    ))
}

@UseCase("model.face.extrude")
private fun extrudeFace(programState: IProgramState): ITask {
    val selection = programState.modelSelection.getOrNull() ?: return TaskNone
    if (selection.selectionType != SelectionType.FACE || selection.size < 1) return TaskNone

    val model = programState.model

    val map = selection.faces.groupBy { it.toObjectRef() }
    val objRefs = selection.faces.map { it.toObjectRef() }.toSet()

    val newSelectionRefs = mutableListOf<IFaceRef>()

    val newModel = model.modifyObjects(objRefs) { ref, obj ->
        val faceRefs = map[ref] ?: return@modifyObjects obj
        val mesh = obj.mesh

        val normal = faceRefs
                .map { mesh.getFacePos(it.faceIndex) }
                .map { pos -> (pos[2] - pos[0]) cross (pos[3] - pos[1]) }
                .reduce { acc, vec -> acc + vec }
                .normalize()

        if (normal.lengthSq() == 0.0) {
            return@modifyObjects obj
        }

        val faces = faceRefs.map { mesh.faces[it.faceIndex] }
        val faceValues = faces.map { it.pos.map { mesh.pos[it] } }

        val allEdges = faces.flatMap { it.getEdges() }

        val edges = allEdges.filter { Collections.frequency(allEdges, it) == 1 }
        val newFacePos = edges.map { edge ->
            val a = mesh.pos[edge.first]
            val b = mesh.pos[edge.second]
            val c = a + normal
            val d = b + normal
            listOf(a, b, d, c)
        }

        var newMesh = mesh.removeFaces(faceRefs.map { it.faceIndex }.toSet())

        newFacePos.forEach { newMesh = newMesh.addFace(it) }

        val base = newMesh.faces.size

        faceValues.forEach { newMesh = newMesh.addFace(it.map { it + normal }) }

        val end = newMesh.faces.size

        newSelectionRefs.addAll((base until end).map { ref.toFaceRef(it) })

        obj.withMesh(newMesh.optimize())
    }

    val newSelection = Selection(SelectionTarget.MODEL, SelectionType.FACE, newSelectionRefs)

    return TaskChain(listOf(
            TaskUpdateModel(oldModel = model, newModel = newModel),

            TaskUpdateModelSelection(
                    oldSelection = programState.modelSelection,
                    newSelection = newSelection.asNullable()),

            TaskUpdateTextureSelection(
                    oldSelection = programState.textureSelection,
                    newSelection = Nullable.castNull())
    ))
}
