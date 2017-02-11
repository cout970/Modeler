package com.cout970.modeler.modeleditor.selection

import com.cout970.matrix.extensions.Matrix4
import com.cout970.modeler.model.*
import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.modeleditor.SelectionMode
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

    var modelSelectionMode: SelectionMode = SelectionMode.ELEMENT
    var modelSelection: Selection = SelectionNone

    var textureSelectionMode: SelectionMode = SelectionMode.QUAD
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

        when (modelSelectionMode) {
            SelectionMode.ELEMENT -> {
                model.getObjectPaths().forEach { path ->
                    val obj = model.getElement(path) as IElementObject

                    obj.rayTrace(Matrix4.IDENTITY, ray)?.let {
                        hits += it to listOf(path)
                    }
                }
            }
            SelectionMode.QUAD -> {
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
            SelectionMode.EDGE -> {
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
            SelectionMode.VERTEX -> {
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

    private fun makeModelSelection(path: List<ElementPath>, allowMultiSelection: Boolean): Selection? {
        if (modelSelectionMode == ModelSelectionMode.GROUP) {
            if (allowMultiSelection && modelSelection.modelMode == ModelSelectionMode.MESH) {
                if (path in modelSelection.paths) {
                    return SelectionGroup(modelSelection.paths - path)
                } else {
                    return SelectionGroup(modelSelection.paths + path)
                }
            } else {
                if (path in modelSelection.paths) {
                    return SelectionNone
                } else {
                    return SelectionGroup(listOf(path))
                }
            }
        } else if (modelSelectionMode == ModelSelectionMode.MESH) {
            if (allowMultiSelection && modelSelection.modelMode == ModelSelectionMode.MESH) {
                if (path in modelSelection.paths) {
                    return SelectionMesh(modelSelection.paths - path)
                } else {
                    return SelectionMesh(modelSelection.paths + path)
                }
            } else {
                if (path in modelSelection.paths) {
                    return SelectionNone
                } else {
                    return SelectionMesh(listOf(path))
                }
            }
        } else if (modelSelectionMode == ModelSelectionMode.QUAD) {
            if (allowMultiSelection && modelSelection.modelMode == ModelSelectionMode.QUAD) {
                if (path in modelSelection.paths) {
                    return SelectionQuad(modelSelection.paths - path)
                } else {
                    return SelectionQuad(modelSelection.paths + path)
                }
            } else {
                if (path in modelSelection.paths) {
                    return SelectionNone
                } else {
                    return SelectionQuad(listOf(path))
                }
            }
        } else if (modelSelectionMode == ModelSelectionMode.VERTEX) {
            if (allowMultiSelection && modelSelection.modelMode == ModelSelectionMode.VERTEX) {
                if (path in modelSelection.paths) {
                    return SelectionVertex(modelSelection.paths - path)
                } else {
                    return SelectionVertex(modelSelection.paths + path)
                }
            } else {
                if (path in modelSelection.paths) {
                    return SelectionNone
                } else {
                    return SelectionVertex(listOf(path))
                }
            }
        }
        return null
    }

    private fun makeTextureSelection(path: List<ElementPath>, allowMultiSelection: Boolean): Selection? {
        if (textureSelectionMode == TextureSelectionMode.QUAD) {
            if (allowMultiSelection && textureSelection.textureMode == TextureSelectionMode.QUAD) {
                if (path in textureSelection.paths) {
                    return SelectionQuad(textureSelection.paths - path)
                } else {
                    return SelectionQuad(textureSelection.paths + path)
                }
            } else {
                if (path in textureSelection.paths) {
                    return SelectionNone
                } else {
                    return SelectionQuad(listOf(path))
                }
            }
        } else if (textureSelectionMode == TextureSelectionMode.VERTEX) {
            if (allowMultiSelection && textureSelection.textureMode == TextureSelectionMode.VERTEX) {
                if (path in textureSelection.paths) {
                    return SelectionVertex(textureSelection.paths - path)
                } else {
                    return SelectionVertex(textureSelection.paths + path)
                }
            } else {
                if (path in textureSelection.paths) {
                    return SelectionNone
                } else {
                    return SelectionVertex(listOf(path))
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
        val hits = mutableListOf<Pair<RayTraceResult, ElementPath>>()
        val model = modelEditor.model

        if (textureSelectionMode == TextureSelectionMode.QUAD) {
            model.getPaths(ElementPath.Level.MESH).forEach { path ->
                val mesh = path.getMesh(model)!!
                val matrix = path.getMeshMatrix(model)
                mesh.getQuads().map { it.transform(matrix) }.forEachIndexed { quadIndex, quad ->
                    RayTraceUtil.rayTraceQuad(ray, mesh,
                            to3D(quad.a.tex),
                            to3D(quad.b.tex),
                            to3D(quad.c.tex),
                            to3D(quad.d.tex)
                    )?.let {
                        hits += it to ElementPath(path.group, path.mesh, quadIndex)
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