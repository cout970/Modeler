package com.cout970.modeler.modeleditor.selection

import com.cout970.matrix.extensions.Matrix4
import com.cout970.modeler.model.*
import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.modeleditor.SelectionMode
import com.cout970.modeler.modeleditor.SelectionTarget
import com.cout970.modeler.modeleditor.action.ActionChangeModelSelection
import com.cout970.modeler.modeleditor.action.ActionChangeTextureSelection
import com.cout970.modeler.util.FakeRayObstacle
import com.cout970.raytrace.Ray
import com.cout970.raytrace.RayTraceResult
import com.cout970.raytrace.RayTraceUtil
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*

/**
 * Created by cout970 on 2016/12/07.
 */
class SelectionManager(val modelEditor: ModelEditor) {

    var selectionMode: SelectionMode = SelectionMode.ELEMENT

    var modelSelectionTarget: SelectionTarget = SelectionTarget.QUAD
    var modelSelection: Selection = SelectionNone

    var textureSelectionTarget: SelectionTarget = SelectionTarget.QUAD
    var textureSelection: Selection = SelectionNone

    fun getMouseHit(ray: Ray, model: Model = modelEditor.model): RayTraceResult? {
        val hits = mutableListOf<Pair<RayTraceResult, ElementPath>>()

        model.getObjectPaths().forEach { path ->
            val obj = model.getElement(path) as IElementObject
            obj.rayTrace(Matrix4.IDENTITY, ray)?.let {
                hits += it to path
            }
        }

        val hit = if (hits.isEmpty()) null
        else if (hits.size == 1) hits.first()
        else hits.apply { sortBy { it.first.hit.distance(ray.start) } }.first()

        return hit?.first
    }

    fun mouseTrySelectModel(ray: Ray, zoom: Float, allowMultiSelection: Boolean) {

        val hits = mutableListOf<Pair<RayTraceResult, List<ElementPath>>>()
        val model = modelEditor.model

        when (selectionMode) {
            SelectionMode.ELEMENT -> {
                model.getObjectPaths().forEach { path ->
                    val obj = model.getElement(path) as IElementObject

                    obj.rayTrace(Matrix4.IDENTITY, ray)?.let {
                        hits += it to listOf(path)
                    }
                }
            }
            SelectionMode.EDIT -> {
                when (modelSelectionTarget) {
                    SelectionTarget.QUAD -> {
                        model.getObjectPaths().forEach { path ->
                            val obj = model.getElement(path) as IElementObject
                            obj.getQuads().forEachIndexed { quadIndex, quad ->
                                RayTraceUtil.rayTraceQuad(ray, FakeRayObstacle,
                                        quad.a.pos, quad.b.pos, quad.c.pos, quad.d.pos)?.let {

                                    hits += it to listOf(
                                            VertexPath(path.indices, obj.faces[quadIndex].a),
                                            VertexPath(path.indices, obj.faces[quadIndex].b),
                                            VertexPath(path.indices, obj.faces[quadIndex].c),
                                            VertexPath(path.indices, obj.faces[quadIndex].d)
                                    )
                                }
                            }
                        }
                    }
                    SelectionTarget.EDGE -> {
                        //TODO add edge selection...
                        model.getObjectPaths().forEach { path ->
                            val obj = model.getElement(path) as IElementObject
                            obj.vertex.forEachIndexed { index, vertexIndex ->
                                val vertex = vertexIndex.toVertex(obj)
                                val start = vertex.pos - vec3Of(0.125) * zoom / 10
                                val end = vertex.pos + vec3Of(0.125) * zoom / 10
                                RayTraceUtil.rayTraceBox3(start, end, ray, FakeRayObstacle)?.let {
                                    hits += it to listOf(VertexPath(path.indices, index))
                                }
                            }
                        }
                    }
                    SelectionTarget.VERTEX -> {
                        model.getObjectPaths().forEach { path ->
                            val obj = model.getElement(path) as IElementObject
                            obj.vertex.forEachIndexed { index, vertexIndex ->
                                val vertex = vertexIndex.toVertex(obj)
                                val start = vertex.pos - vec3Of(0.125) * zoom / 10
                                val end = vertex.pos + vec3Of(0.125) * zoom / 10
                                RayTraceUtil.rayTraceBox3(start, end, ray, FakeRayObstacle)?.let {
                                    hits += it to listOf(VertexPath(path.indices, index))
                                }
                            }
                        }
                    }
                }
            }
        }

        val hit = if (hits.isEmpty()) null
        else if (hits.size == 1) hits.first()
        else hits.apply { sortBy { it.first.hit.distance(ray.start) } }.first()

        if (hit != null) {
            val sel = handleModelSelection(hit.second, allowMultiSelection)
            updateModelSelection(sel)
        } else {
            if (!allowMultiSelection) {
                clearModelSelection()
            }
        }
    }

    fun updateModelSelection(sel: Selection) {
        modelEditor.historyRecord.doAction(ActionChangeModelSelection(modelSelection, sel, modelEditor))
        updateTextureSelection(sel)
    }

    fun updateTextureSelection(sel: Selection) {
        modelEditor.historyRecord.doAction(ActionChangeTextureSelection(textureSelection, sel, modelEditor))
    }

    fun handleModelSelection(path: List<ElementPath>, allowMultiSelection: Boolean): Selection {
        var sel = makeModelSelection(path, allowMultiSelection)
        if (sel == null || sel.paths.isEmpty()) sel = SelectionNone
        return sel
    }

    fun handleTextureSelection(path: List<ElementPath>, allowMultiSelection: Boolean): Selection {
        var sel = makeTextureSelection(path, allowMultiSelection)
        if (sel == null || sel.paths.isEmpty()) sel = SelectionNone
        return sel
    }

    @Suppress("UNCHECKED_CAST")
    private fun makeModelSelection(paths: List<ElementPath>, allowMultiSelection: Boolean): Selection? {
        when (selectionMode) {
            SelectionMode.ELEMENT -> {
                if (allowMultiSelection && modelSelection.mode == SelectionMode.ELEMENT) {
                    var list: List<ElementPath> = modelSelection.paths

                    if (paths.all { it in list }) {
                        for (i in paths) {
                            list -= i
                        }
                    } else {
                        for (i in paths) {
                            list += i
                        }
                    }

                    if (list.isEmpty()) {
                        return SelectionNone
                    } else {
                        return Selection(list)
                    }
                } else {
                    if (paths.isEmpty()) {
                        return SelectionNone
                    } else {
                        return Selection(paths)
                    }
                }
            }
            SelectionMode.EDIT -> {
                if (allowMultiSelection && modelSelection.mode == SelectionMode.EDIT) {
                    var list: List<VertexPath> = modelSelection.paths as List<VertexPath>

                    if (paths.all { it in list }) {
                        for (i in paths) {
                            list -= i as VertexPath
                        }
                    } else {
                        for (i in paths) {
                            list += i as VertexPath
                        }
                    }
                    if (list.isEmpty()) {
                        return SelectionNone
                    } else {
                        return VertexSelection(list)
                    }
                } else {
                    return VertexSelection(paths as List<VertexPath>)
                }
            }
        }
        return null
    }

    @Suppress("UNCHECKED_CAST")
    private fun makeTextureSelection(paths: List<ElementPath>, allowMultiSelection: Boolean): Selection? {
        when (selectionMode) {
            SelectionMode.ELEMENT -> {
                if (allowMultiSelection && textureSelection.mode == SelectionMode.ELEMENT) {
                    var list: List<ElementPath> = textureSelection.paths
                    if (paths.all { it in list }) {
                        for (i in paths) {
                            list -= i as VertexPath
                        }
                    } else {
                        for (i in paths) {
                            list += i as VertexPath
                        }
                    }
                    if (list.isEmpty()) {
                        return SelectionNone
                    } else {
                        return Selection(list)
                    }
                } else {

                }
            }
            SelectionMode.EDIT -> {
                if (allowMultiSelection && textureSelection.mode == SelectionMode.EDIT) {
                    var list: List<VertexPath> = textureSelection.paths as List<VertexPath>
                    if (paths.all { it in list }) {
                        for (i in paths) {
                            list -= i as VertexPath
                        }
                    } else {
                        for (i in paths) {
                            list += i as VertexPath
                        }
                    }
                    if (list.isEmpty()) {
                        return SelectionNone
                    } else {
                        return VertexSelection(list)
                    }
                } else {
                    return VertexSelection(paths as List<VertexPath>)
                }
            }
        }
        return null
    }

    fun clearModelSelection() {
        updateModelSelection(SelectionNone)
    }

    fun clearTextureSelection() {
        updateTextureSelection(SelectionNone)
    }

    fun mouseTrySelectTexture(ray: Ray, zoom: Float, allowMultiSelection: Boolean, to3D: (IVector2) -> IVector3) {
        val hits = mutableListOf<Pair<RayTraceResult, List<ElementPath>>>()
        val model = modelEditor.model

        model.getObjectPaths().forEach { path ->
            val element = model.getElement(path)
            element.getQuads().forEachIndexed { i, quad ->
                RayTraceUtil.rayTraceQuad(ray, FakeRayObstacle,
                        to3D(quad.a.tex),
                        to3D(quad.b.tex),
                        to3D(quad.c.tex),
                        to3D(quad.d.tex)
                )?.let {
                    if (textureSelectionTarget == SelectionTarget.QUAD) {
                        hits += it to path.getSubPaths(model)
                    } else {
                        hits += it to listOf(path)
                    }
                }
            }
        }

        val hit = if (hits.isEmpty()) null
        else if (hits.size == 1) hits.first()
        else hits.apply { sortBy { it.first.hit.distance(ray.start) } }.first()

        if (hit != null) {
            val sel = handleTextureSelection(hit.second, allowMultiSelection)
            updateTextureSelection(sel)
        } else {
            if (!allowMultiSelection) {
                clearTextureSelection()
            }
        }
    }
}