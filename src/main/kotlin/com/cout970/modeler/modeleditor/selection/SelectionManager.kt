package com.cout970.modeler.modeleditor.selection

import com.cout970.modeler.model.Model
import com.cout970.modeler.model.Vertex
import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.modeleditor.action.ActionChangeModelSelection
import com.cout970.modeler.modeleditor.action.ActionChangeTextureSelection
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

    var modelSelectionMode: ModelSelectionMode = ModelSelectionMode.MESH
    var modelSelection: IModelSelection = SelectionNone

    var textureSelectionMode: TextureSelectionMode = TextureSelectionMode.QUAD
    var textureSelection: ITextureSelection = SelectionNone

    fun getMouseHit(ray: Ray, model: Model = modelEditor.model): RayTraceResult? {
        val hits = mutableListOf<Pair<RayTraceResult, ModelPath>>()

        model.getPaths(ModelPath.Level.MESH).forEach { path ->
            path.getMesh(model)!!.rayTrace(path.getMeshMatrix(model), ray)?.let {
                hits += it to ModelPath(path.group)
            }
        }

        val hit = if (hits.isEmpty()) null
        else if (hits.size == 1) hits.first()
        else hits.apply { sortBy { it.first.hit.distance(ray.start) } }.first()

        return hit?.first
    }

    fun mouseTrySelectModel(ray: Ray, zoom: Float, allowMultiSelection: Boolean) {

        val hits = mutableListOf<Pair<RayTraceResult, ModelPath>>()
        val model = modelEditor.model

        if (modelSelectionMode == ModelSelectionMode.GROUP) {
            model.getPaths(ModelPath.Level.MESH).forEach { path ->
                path.getMesh(model)!!.rayTrace(path.getMeshMatrix(model), ray)?.let {
                    hits += it to ModelPath(path.group)
                }
            }
        } else if (modelSelectionMode == ModelSelectionMode.MESH) {
            model.getPaths(ModelPath.Level.MESH).forEach { path ->
                path.getMesh(model)!!.rayTrace(path.getMeshMatrix(model), ray)?.let {
                    hits += it to path
                }
            }
        } else if (modelSelectionMode == ModelSelectionMode.QUAD) {
            model.getPaths(ModelPath.Level.MESH).forEach { path ->
                val mesh = path.getMesh(model)!!
                val matrix = path.getMeshMatrix(model)
                mesh.getQuads().map { it.transform(matrix) }.forEachIndexed { quadIndex, quad ->
                    RayTraceUtil.rayTraceQuad(ray, mesh, quad.a.pos, quad.b.pos, quad.c.pos, quad.d.pos)?.let {
                        hits += it to ModelPath(path.group, path.mesh, quadIndex)
                    }
                }
            }
        } else if (modelSelectionMode == ModelSelectionMode.VERTEX) {
            model.getPaths(ModelPath.Level.MESH).forEach { path ->
                val mesh = path.getMesh(model)!!
                val matrix = path.getMeshMatrix(model)
                mesh.indices.forEachIndexed { quadIndex, quadI ->
                    val quad = quadI.toQuad(mesh.positions, mesh.textures).transform(matrix)

                    fun rayTraceVertex(vertex: Vertex, index: Int) {
                        val start = vertex.pos - vec3Of(0.125) * zoom / 10
                        val end = vertex.pos + vec3Of(0.125) * zoom / 10
                        RayTraceUtil.rayTraceBox3(start, end, ray, mesh)?.let {
                            hits += it to ModelPath(path.group, path.mesh, quadIndex, index)
                        }
                    }
                    rayTraceVertex(quad.a, 0)
                    rayTraceVertex(quad.b, 1)
                    rayTraceVertex(quad.c, 2)
                    rayTraceVertex(quad.d, 3)
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

    fun updateModelSelection(sel: IModelSelection) {
        modelEditor.historyRecord.doAction(ActionChangeModelSelection(modelSelection, sel, modelEditor))
        updateTextureSelection(sel.toTextureSelection(modelEditor.model))
    }

    fun updateTextureSelection(sel: ITextureSelection) {
        modelEditor.historyRecord.doAction(ActionChangeTextureSelection(textureSelection, sel, modelEditor))
    }

    fun handleModelSelection(path: ModelPath, allowMultiSelection: Boolean): IModelSelection {
        var sel = makeModelSelection(path, allowMultiSelection)
        if (sel == null || sel.paths.isEmpty()) sel = SelectionNone
        return sel
    }

    fun handleTextureSelection(path: ModelPath, allowMultiSelection: Boolean): ITextureSelection {
        var sel = makeTextureSelection(path, allowMultiSelection)
        if (sel == null || sel.paths.isEmpty()) sel = SelectionNone
        return sel
    }

    private fun makeModelSelection(path: ModelPath, allowMultiSelection: Boolean): IModelSelection? {
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

    private fun makeTextureSelection(path: ModelPath, allowMultiSelection: Boolean): ITextureSelection? {
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
        val hits = mutableListOf<Pair<RayTraceResult, ModelPath>>()
        val model = modelEditor.model

        if (textureSelectionMode == TextureSelectionMode.QUAD) {
            model.getPaths(ModelPath.Level.MESH).forEach { path ->
                val mesh = path.getMesh(model)!!
                val matrix = path.getMeshMatrix(model)
                mesh.getQuads().map { it.transform(matrix) }.forEachIndexed { quadIndex, quad ->
                    RayTraceUtil.rayTraceQuad(ray, mesh,
                            to3D(quad.a.tex),
                            to3D(quad.b.tex),
                            to3D(quad.c.tex),
                            to3D(quad.d.tex)
                    )?.let {
                        hits += it to ModelPath(path.group, path.mesh, quadIndex)
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