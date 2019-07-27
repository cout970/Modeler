package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.model.`object`.RootGroupRef
import com.cout970.modeler.api.model.selection.*
import com.cout970.modeler.controller.tasks.*
import com.cout970.modeler.core.animation.AnimationNone
import com.cout970.modeler.core.helpers.invert
import com.cout970.modeler.core.model.`object`.Object
import com.cout970.modeler.core.model.faces
import com.cout970.modeler.core.model.getGlobalMesh
import com.cout970.modeler.core.model.getSelectedObjects
import com.cout970.modeler.core.model.mesh.FaceIndex
import com.cout970.modeler.core.model.mesh.Mesh
import com.cout970.modeler.core.model.objects
import com.cout970.modeler.core.model.selection.Selection
import com.cout970.modeler.core.project.IProgramState
import com.cout970.modeler.render.tool.*
import com.cout970.modeler.util.*
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.*
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.TextInput
import java.util.*
import kotlin.math.max

/**
 * Created by cout970 on 2017/10/29.
 */

@UseCase("model.obj.change.name")
private fun changeObjectName(component: Component, programState: IProgramState): ITask {
    val model = programState.model
    val selection = programState.modelSelection
    val ref = component.metadata["ref"] ?: selection.flatMap { it.objects.firstOrNull() }.getOrNull() ?: return TaskNone

    val textInput = component as? TextInput ?: return TaskNone
    val name = textInput.text

    val newModel = model.modifyObjects({ it == ref }) { _, obj ->
        obj.withName(name)
    }

    return TaskUpdateModel(newModel = newModel, oldModel = model)
}

@UseCase("model.group.change.name")
private fun changeGroupName(component: Component, programState: IProgramState): ITask {
    val model = programState.model
    val groupRef = programState.selectedGroup
    if (groupRef == RootGroupRef) return TaskNone

    val name = component.asNullable()
        .filterIsInstance<TextInput>()
        .map { it.text }
        .filter { !it.isBlank() }
        .getOrNull() ?: return TaskNone

    val group = model.getGroup(groupRef)
    val newModel = model.modifyGroup(group.withName(name))

    return TaskUpdateModel(newModel = newModel, oldModel = model)
}


@UseCase("model.obj.join")
private fun joinObjects(programState: IProgramState): ITask {
    val selection = programState.modelSelection.getOrNull() ?: return TaskNone
    if (selection.selectionType != SelectionType.OBJECT || selection.size < 2) return TaskNone

    val model = programState.model
    val objs = model.getSelectedObjects(selection)
    val objsRefs = selection.objects

    val baseObj = objs.first()

    val objMeshes = objs.map { obj ->
        obj.mesh.transform(obj.transformation).transform(baseObj.transformation.invert())
    }
    val mesh = objMeshes.reduce { acc, mesh -> acc.merge(mesh) }

    val newObj = Object(
        name = objs.first().name,
        mesh = mesh,
        material = baseObj.material,
        transformation = baseObj.transformation
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

@UseCase("model.obj.split")
private fun splitObjects(programState: IProgramState): ITask {
    val selection = programState.modelSelection.getOrNull() ?: return TaskNone
    if (selection.selectionType == SelectionType.OBJECT) return TaskNone

    // TODO note: this is a copy of joinObjects
    val model = programState.model
    val objs = model.getSelectedObjects(selection)
    val objsRefs = selection.objects

    val baseObj = objs.first()

    val objMeshes = objs.map { obj ->
        obj.mesh.transform(obj.transformation).transform(baseObj.transformation.invert())
    }
    val mesh = objMeshes.reduce { acc, mesh -> acc.merge(mesh) }

    val newObj = Object(
        name = objs.first().name,
        mesh = mesh,
        material = baseObj.material,
        transformation = baseObj.transformation
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
private fun arrangeUVs(programState: IProgramState, animator: Animator): ITask {
    val selection = programState.modelSelection.getOrNull() ?: return TaskNone
    if (selection.selectionType != SelectionType.OBJECT) return TaskNone

    val model = programState.model
    var lastY = 0.0

    val newModel = model.modifyObjects(selection.objects.toSet()) { _, obj ->
        val mesh = obj.getGlobalMesh(model, animator, AnimationNone)
        val newTex = mutableListOf<IVector2>()
        var sizeY = 0.0
        var lastX = 0.0

        val newFaces = mesh.faces.mapIndexed { faceIndex, face ->
            val a = mesh.pos[face.pos[0]]
            val b = mesh.pos[face.pos[1]]
            val c = mesh.pos[face.pos[2]]
            val d = mesh.pos[face.pos[3]]

            val ac = c - a
            val bd = d - b
            val normal = -(ac cross bd).normalize()

            // 3d axis representing the 2d axis in the plane space
            val orthoX = (-(d - c).normalize())
                .takeIf { it.hasNaN() } ?: (if (faceIndex.isEven()) -(a - b).normalize() else (b - c).normalize())

            val orthoY = (orthoX cross normal)

            var sizeX = 0.0
            repeat(4) { index ->
                val point3d = mesh.pos[face.pos[index]]
                val relPoint = point3d - a

                val x = orthoX.dot(relPoint) * 1f / 16f
                val y = orthoY.dot(relPoint) * 1f / 16f

                newTex += vec2Of(lastX + x, lastY + y)
                sizeX = max(sizeX, x)
                sizeY = max(sizeY, y)
            }
            lastX = (lastX + sizeX).roundTo(32.0)

            FaceIndex.from(face.pos, listOf(newTex.size - 4, newTex.size - 3, newTex.size - 2, newTex.size - 1))
        }

        lastY = (lastY + sizeY).roundTo(16.0)

        obj.withMesh(Mesh(
            pos = obj.mesh.pos,
            tex = newTex,
            faces = newFaces
        ))
    }

    return TaskChain(listOf(
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
