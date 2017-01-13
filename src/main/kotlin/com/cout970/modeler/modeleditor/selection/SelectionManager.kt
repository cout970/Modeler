package com.cout970.modeler.modeleditor.selection

import com.cout970.glutilities.device.Keyboard
import com.cout970.modeler.config.Config
import com.cout970.modeler.model.Vertex
import com.cout970.modeler.modeleditor.ModelController
import com.cout970.modeler.modeleditor.action.ActionChangeSelection
import com.cout970.raytrace.Ray
import com.cout970.raytrace.RayTraceResult
import com.cout970.raytrace.RayTraceUtil
import com.cout970.vector.extensions.*

/**
 * Created by cout970 on 2016/12/07.
 */
class SelectionManager(val modelController: ModelController) {

    var selectionMode: SelectionMode = SelectionMode.MESH
    var selection: Selection = SelectionNone

    fun getMouseHit(ray: Ray): RayTraceResult? {
        val hits = mutableListOf<Pair<RayTraceResult, ModelPath>>()
        val model = modelController.model

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

    fun mouseTrySelect(ray: Ray, zoom: Float) {

        val hits = mutableListOf<Pair<RayTraceResult, ModelPath>>()
        val model = modelController.model

        if (selectionMode == SelectionMode.GROUP) {
            model.getPaths(ModelPath.Level.MESH).forEach { path ->
                path.getMesh(model)!!.rayTrace(path.getMeshMatrix(model), ray)?.let {
                    hits += it to ModelPath(path.group)
                }
            }
        } else if (selectionMode == SelectionMode.MESH) {
            model.getPaths(ModelPath.Level.MESH).forEach { path ->
                path.getMesh(model)!!.rayTrace(path.getMeshMatrix(model), ray)?.let {
                    hits += it to path
                }
            }
        } else if (selectionMode == SelectionMode.QUAD) {
            model.getPaths(ModelPath.Level.MESH).forEach { path ->
                val mesh = path.getMesh(model)!!
                val matrix = path.getMeshMatrix(model)
                mesh.getQuads().map { it.transform(matrix) }.forEachIndexed { quadIndex, quad ->
                    RayTraceUtil.rayTraceQuad(ray, mesh, quad.a.pos, quad.b.pos, quad.c.pos, quad.d.pos)?.let {
                        hits += it to ModelPath(path.group, path.mesh, quadIndex)
                    }
                }
            }
        } else if (selectionMode == SelectionMode.VERTEX) {
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
                    rayTraceVertex(quad.a, quadI.aP)
                    rayTraceVertex(quad.b, quadI.bP)
                    rayTraceVertex(quad.c, quadI.cP)
                    rayTraceVertex(quad.d, quadI.dP)
                }
            }
        }

        val hit = if (hits.isEmpty()) null
        else if (hits.size == 1) hits.first()
        else hits.apply { sortBy { it.first.hit.distance(ray.start) } }.first()

        if (hit != null) {
            val sel = handleSelection(hit.second)
            updateSelection(sel)
        } else {
            if (!modelController.eventController.keyboard.isKeyPressed(Keyboard.KEY_LEFT_CONTROL)) {
                updateSelection(SelectionNone)
            }
        }
    }

    fun updateSelection(sel: Selection) {
        modelController.historyRecord.doAction(ActionChangeSelection(selection, sel, modelController))
    }

    fun handleSelection(path: ModelPath): Selection {
        var sel = makeSelection(path)
        if (sel == null || sel.paths.isEmpty()) sel = SelectionNone
        return sel
    }

    private fun makeSelection(path: ModelPath): Selection? {
        if (selectionMode == SelectionMode.GROUP) {
            if (Config.keyBindings.multipleSelection.check(
                    modelController.eventController.keyboard) && selection.mode == SelectionMode.MESH) {
                if (path in selection.paths) {
                    return SelectionGroup(selection.paths - path)
                } else {
                    return SelectionGroup(selection.paths + path)
                }
            } else {
                if (path in selection.paths) {
                    return SelectionNone
                } else {
                    return SelectionGroup(listOf(path))
                }
            }
        } else if (selectionMode == SelectionMode.MESH) {
            if (Config.keyBindings.multipleSelection.check(
                    modelController.eventController.keyboard) && selection.mode == SelectionMode.MESH) {
                if (path in selection.paths) {
                    return SelectionComponent(selection.paths - path)
                } else {
                    return SelectionComponent(selection.paths + path)
                }
            } else {
                if (path in selection.paths) {
                    return SelectionNone
                } else {
                    return SelectionComponent(listOf(path))
                }
            }
        } else if (selectionMode == SelectionMode.QUAD) {
            if (Config.keyBindings.multipleSelection.check(
                    modelController.eventController.keyboard) && selection.mode == SelectionMode.QUAD) {
                if (path in selection.paths) {
                    return SelectionQuad(selection.paths - path)
                } else {
                    return SelectionQuad(selection.paths + path)
                }
            } else {
                if (path in selection.paths) {
                    return SelectionNone
                } else {
                    return SelectionQuad(listOf(path))
                }
            }
        } else if (selectionMode == SelectionMode.VERTEX) {
            if (Config.keyBindings.multipleSelection.check(
                    modelController.eventController.keyboard) && selection.mode == SelectionMode.VERTEX) {
                if (path in selection.paths) {
                    return SelectionVertex(selection.paths - path)
                } else {
                    return SelectionVertex(selection.paths + path)
                }
            } else {
                if (path in selection.paths) {
                    return SelectionNone
                } else {
                    return SelectionVertex(listOf(path))
                }
            }
        }
        return null
    }

    fun clearSelection() {
        selection = SelectionNone
    }
}