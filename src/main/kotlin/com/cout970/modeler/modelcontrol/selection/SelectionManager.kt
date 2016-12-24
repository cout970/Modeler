package com.cout970.modeler.modelcontrol.selection

import com.cout970.glutilities.device.Keyboard
import com.cout970.modeler.modelcontrol.ModelController
import com.cout970.modeler.modelcontrol.action.ActionChangeSelection
import com.cout970.raytrace.Ray
import com.cout970.raytrace.RayTraceResult
import com.cout970.raytrace.RayTraceUtil
import com.cout970.vector.extensions.distance
import com.cout970.vector.extensions.minus
import com.cout970.vector.extensions.plus
import com.cout970.vector.extensions.vec3Of

/**
 * Created by cout970 on 2016/12/07.
 */
class SelectionManager(val modelController: ModelController) {

    var selectionMode: SelectionMode = SelectionMode.COMPONENT
    var selection: Selection = SelectionNone

    fun mouseTrySelect(ray: Ray) {

        val hits = mutableListOf<Pair<RayTraceResult, ModelPath>>()
        val model = modelController.model

        if (selectionMode == SelectionMode.GROUP) {
            model.getPaths(ModelPath.Level.COMPONENTS).forEach { path ->
                path.getComponent(model)!!.rayTrace(path.getComponentMatrix(model), ray)?.let {
                    hits += it to ModelPath(path.obj, path.group)
                }
            }
        } else if (selectionMode == SelectionMode.COMPONENT) {
            model.getPaths(ModelPath.Level.COMPONENTS).forEach { path ->
                path.getComponent(model)!!.rayTrace(path.getComponentMatrix(model), ray)?.let {
                    hits += it to path
                }
            }
        } else if (selectionMode == SelectionMode.QUAD) {
            model.getPaths(ModelPath.Level.COMPONENTS).forEach { path ->
                val comp = path.getComponent(model)!!
                val matrix = path.getComponentMatrix(model)
                comp.getQuads().map { it.transform(matrix) }.forEachIndexed { quadIndex, quad ->
                    RayTraceUtil.rayTraceQuad(ray, comp, quad.a.pos, quad.b.pos, quad.c.pos, quad.d.pos)?.let {
                        hits += it to ModelPath(path.obj, path.group, path.component, quadIndex)
                    }
                }
            }
        } else if (selectionMode == SelectionMode.VERTEX) {
            model.getPaths(ModelPath.Level.COMPONENTS).forEach { path ->
                val comp = path.getComponent(model)!!
                val matrix = path.getComponentMatrix(model)
                comp.getQuads().map { it.transform(matrix) }.mapIndexed { quadIndex, quad ->
                    quad.vertex.forEachIndexed { vertexIndex, vertex ->
                        val start = vertex.pos - vec3Of(0.125)
                        val end = vertex.pos + vec3Of(0.125)
                        RayTraceUtil.rayTraceBox3(start, end, ray, comp)?.let {
                            hits += it to ModelPath(path.obj, path.group, path.component, quadIndex, vertexIndex)
                        }
                    }
                }
            }
        }

        val hit = if (hits.isEmpty()) null
        else if (hits.size == 1) hits.first()
        else hits.apply { sortBy { it.first.hit.distance(ray.start) } }.first()

        if (hit != null) {
            val sel = handleSelection(hit.second)
            modelController.historyRecord.doAction(ActionChangeSelection(selection, sel, modelController))
        } else {
            if (!modelController.eventController.keyboard.isKeyPressed(Keyboard.KEY_LEFT_CONTROL)) {
                modelController.historyRecord.doAction(ActionChangeSelection(selection, SelectionNone, modelController))
            }
        }
    }

    fun handleSelection(path: ModelPath): Selection {
        var sel = makeSelection(path)
        if (sel == null || sel.paths.isEmpty()) sel = SelectionNone
        return sel
    }

    private fun makeSelection(path: ModelPath): Selection? {
        if (selectionMode == SelectionMode.GROUP) {
            if (modelController.eventController.keyboard.isKeyPressed(Keyboard.KEY_LEFT_CONTROL) && selection.mode == SelectionMode.COMPONENT) {
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
        } else if (selectionMode == SelectionMode.COMPONENT) {
            if (modelController.eventController.keyboard.isKeyPressed(Keyboard.KEY_LEFT_CONTROL) && selection.mode == SelectionMode.COMPONENT) {
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
            if (modelController.eventController.keyboard.isKeyPressed(Keyboard.KEY_LEFT_CONTROL) && selection.mode == SelectionMode.QUAD) {
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
            if (modelController.eventController.keyboard.isKeyPressed(Keyboard.KEY_LEFT_CONTROL) && selection.mode == SelectionMode.VERTEX) {
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