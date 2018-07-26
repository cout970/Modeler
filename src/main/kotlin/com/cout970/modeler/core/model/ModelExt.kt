package com.cout970.modeler.core.model

import com.cout970.matrix.api.IMatrix4
import com.cout970.matrix.extensions.Matrix4
import com.cout970.matrix.extensions.times
import com.cout970.modeler.api.animation.IAnimation
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.api.model.`object`.*
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.api.model.selection.*
import com.cout970.modeler.core.model.`object`.BiMultimap
import com.cout970.modeler.core.model.`object`.ObjectNone
import com.cout970.modeler.core.model.`object`.emptyBiMultimap
import com.cout970.modeler.core.model.material.MaterialNone
import com.cout970.modeler.core.model.material.MaterialRef
import com.cout970.modeler.core.model.material.MaterialRefNone
import com.cout970.modeler.core.model.selection.ObjectRef
import com.cout970.modeler.core.model.selection.ObjectRefNone
import com.cout970.modeler.render.tool.Animator
import com.cout970.modeler.util.middle
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2017/06/09.
 */

val ISelection.pos: List<IPosRef> get() = refs.filterIsInstance<IPosRef>()
val ISelection.edges: List<IEdgeRef> get() = refs.filterIsInstance<IEdgeRef>()
val ISelection.faces: List<IFaceRef> get() = refs.filterIsInstance<IFaceRef>()
val ISelection.objects: List<IObjectRef> get() = refs.filterIsInstance<IObjectRef>()

fun IObject.toPair(): Pair<IObjectRef, IObject> = Pair(ObjectRef(id), this)

inline val IObject.ref: IObjectRef get() = if (id == ObjectNone.id) ObjectRefNone else ObjectRef(id)
inline val IMaterial.ref: IMaterialRef get() = if (id == MaterialNone.id) MaterialRefNone else MaterialRef(id)
inline val IGroup.ref: IGroupRef get() = if (id == GroupNone.id) RootGroupRef else GroupRef(id)

fun IObject.getGlobalMesh(model: IModel, animator: Animator, animation: IAnimation = animator.animation): IMesh {
    val mat = getRecursiveMatrix(ref, model, animator, animation) ?: return mesh
    return mesh.transform(mat)
}

fun IObject.getGlobalMatrix(model: IModel, animator: Animator, animation: IAnimation = animator.animation): IMatrix4 {
    return getRecursiveMatrix(ref, model, animator, animation) ?: Matrix4.IDENTITY
}

fun IObject.getParentGlobalMatrix(model: IModel, animator: Animator, animation: IAnimation = animator.animation): IMatrix4 {
    return getParentRecursiveMatrix(ref, model, animator, animation) ?: Matrix4.IDENTITY
}

fun IObject.getGlobalTransform(model: IModel): ITransformation {
    return getRecursiveTransform(ref, model) ?: TRSTransformation.IDENTITY
}

fun IModel.getSelectionCenter(selection: ISelection, animator: Animator,
                              animation: IAnimation = animator.animation): IVector3 {

    return when (selection.selectionType) {
        SelectionType.OBJECT -> getSelectedObjects(selection)
                .map { it.getGlobalMesh(this, animator, animation).middle() }
                .middle()

        SelectionType.FACE -> {
            selection.faces
                    .groupBy { it.toObjectRef() to it }
                    .map { (pair, list) -> getObject(pair.first).getGlobalMesh(this, animator, animation) to list }
                    .flatMap { (mesh, list) ->
                        list.map { ref ->
                            mesh.faces[ref.faceIndex]
                                    .pos
                                    .mapNotNull { mesh.pos.getOrNull(it) }
                                    .middle()
                        }
                    }
                    .middle()
        }

        SelectionType.EDGE -> {
            selection.edges
                    .groupBy { it.toObjectRef() to it }
                    .map { (pair, list) -> getObject(pair.first).getGlobalMesh(this, animator, animation) to list }
                    .flatMap { (mesh, list) -> list.flatMap { listOf(mesh.pos[it.firstIndex], mesh.pos[it.secondIndex]) } }
                    .middle()
        }

        SelectionType.VERTEX -> {
            selection.pos
                    .groupBy { it.toObjectRef() to it }
                    .map { (pair, list) -> getObject(pair.first).getGlobalMesh(this, animator, animation) to list }
                    .flatMap { (mesh, list) -> list.map { mesh.pos[it.posIndex] } }
                    .middle()
        }
    }
}

private fun getRecursiveMatrix(ref: IObjectRef, model: IModel, animator: Animator, animation: IAnimation): IMatrix4? {
    model.tree.objects[RootGroupRef].forEach { obj ->
        if (obj == ref) return model.getObject(obj).transformation.matrix * animator.animate(animation, RootGroupRef, obj)
    }

    model.tree.groups[RootGroupRef].forEach {
        val mat = getRecursiveMatrix(ref, model, it, Matrix4.IDENTITY, animator, animation)
        if (mat != null) return mat
    }
    return null
}

private fun getRecursiveMatrix(ref: IObjectRef, model: IModel, group: IGroupRef,
                               matrix: IMatrix4, animator: Animator, animation: IAnimation): IMatrix4? {

    val mat = model.getGroup(group).transform.matrix * matrix * animator.animate(animation, group, ObjectRefNone)

    model.tree.objects[group].forEach { obj ->
        if (obj == ref) return mat * model.getObject(obj).transformation.matrix * animator.animate(animation, group, obj)
    }

    model.tree.groups[group].forEach {
        val newMat = getRecursiveMatrix(ref, model, it, mat, animator, animation)
        if (newMat != null) return newMat
    }
    return null
}

private fun getParentRecursiveMatrix(ref: IObjectRef, model: IModel, animator: Animator, animation: IAnimation): IMatrix4? {
    model.tree.objects[RootGroupRef].forEach { obj ->
        if (obj == ref) return null
    }

    model.tree.groups[RootGroupRef].forEach {
        val mat = getParentRecursiveMatrix(ref, model, it, Matrix4.IDENTITY, animator, animation)
        if (mat != null) return mat
    }
    return null
}

private fun getParentRecursiveMatrix(ref: IObjectRef, model: IModel, group: IGroupRef,
                                     matrix: IMatrix4, animator: Animator, animation: IAnimation): IMatrix4? {

    val mat = model.getGroup(group).transform.matrix * matrix * animator.animate(animation, group, ObjectRefNone)

    model.tree.objects[group].forEach { obj ->
        if (obj == ref) return mat
    }

    model.tree.groups[group].forEach {
        val newMat = getParentRecursiveMatrix(ref, model, it, mat, animator, animation)
        if (newMat != null) return newMat
    }
    return null
}

private fun getRecursiveTransform(ref: IObjectRef, model: IModel): ITransformation? {
    model.tree.objects[RootGroupRef].forEach { obj ->
        if (obj == ref) return model.getObject(obj).transformation
    }

    model.tree.groups[RootGroupRef].forEach {
        val mat = getRecursiveTransform(ref, model, it, TRSTransformation.IDENTITY)
        if (mat != null) return mat
    }
    return null
}

private fun getRecursiveTransform(ref: IObjectRef, model: IModel, group: IGroupRef, matrix: TRSTransformation): ITransformation? {

    val mat = model.getGroup(group).transform + matrix

    model.tree.objects[group].forEach { obj ->
        if (obj == ref) return mat + model.getObject(obj).transformation
    }

    model.tree.groups[group].forEach {
        val newMat = getRecursiveTransform(ref, model, it, mat.toTRS())
        if (newMat != null) return newMat
    }
    return null
}

fun IModel.getSelectedObjects(sel: ISelection): List<IObject> =
        sel.refs.filterIsInstance<IObjectRef>().map { getObject(it) }

fun IModel.getRecursiveChildObjects(group: IGroupRef): List<IObjectRef> = subTree(tree, group).getChildObjects()

fun MutableGroupTree.getChildObjects(): List<IObjectRef> {
    return this.objects + children.flatMap { it.getChildObjects() }
}

fun IModel.getRecursiveChildGroups(group: IGroupRef): List<IGroupRef> = subTree(tree, group).getChildGroups()

fun MutableGroupTree.getChildGroups(): List<IGroupRef> {
    return listOf(this.group) + children.flatMap { it.getChildGroups() }
}

data class MutableWrapper<T>(var value: T) {
    fun mutate(func: T.() -> T) {
        value = func(value)
    }
}

fun MutableGroupTree.toImmutable(): ImmutableGroupTree {
    val objectMap = MutableWrapper<BiMultimap<IGroupRef, IObjectRef>>(emptyBiMultimap())
    val groupMap = MutableWrapper<BiMultimap<IGroupRef, IGroupRef>>(emptyBiMultimap())

    superTree(this, objectMap, groupMap)

    return ImmutableGroupTree(objectMap.value, groupMap.value)
}

fun ImmutableGroupTree.toMutable(): MutableGroupTree = subTree(this, RootGroupRef)

fun ImmutableGroupTree.mutate(func: MutableGroupTree.() -> Unit): ImmutableGroupTree =
        toMutable().apply(func).toImmutable()

fun MutableGroupTree.removeObjects(refs: Set<IObjectRef>) {
    objects.removeAll(refs)
    children.forEach { it.removeObjects(refs) }
}

fun MutableGroupTree.removeGroup(ref: IGroupRef) {
    children.removeAll { it.group == ref }
    children.forEach { it.removeGroup(ref) }
}

fun MutableGroupTree.addObject(obj: IObjectRef, parent: IGroupRef): Boolean {
    if (group == parent) {
        objects.add(obj)
        return true
    }
    return children.any { it.addObject(obj, parent) }
}

fun MutableGroupTree.changeParent(child: IGroupRef, parent: IGroupRef): Boolean {
    val parentNode = findChild(parent) ?: return false
    val childNode = findChild(child) ?: return false

    // if parent in children: crash()

    removeGroup(child)
    parentNode.children.add(childNode)

    return true
}

fun MutableGroupTree.contains(child: MutableGroupTree): Boolean {
    if (this == child) return true
    return children.any { it.contains(child) }
}

fun MutableGroupTree.findChild(node: IGroupRef): MutableGroupTree? {
    if (node == group) return this
    return children.asSequence().mapNotNull { it.findChild(node) }.firstOrNull()
}

private fun superTree(
        tree: MutableGroupTree,
        objectMap: MutableWrapper<BiMultimap<IGroupRef, IObjectRef>>,
        groupMap: MutableWrapper<BiMultimap<IGroupRef, IGroupRef>>) {

    objectMap.mutate { addAll(tree.group, tree.objects) }
    groupMap.mutate { addAll(tree.group, tree.children.map { it.group }) }

    tree.children.forEach { superTree(it, objectMap, groupMap) }
}


private fun subTree(tree: ImmutableGroupTree, group: IGroupRef): MutableGroupTree = MutableGroupTree(
        objects = tree.objects[group].toMutableList(),
        group = group,
        children = tree.groups[group].map { subTree(tree, it) }.toMutableList()
)