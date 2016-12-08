package com.cout970.modeler.modelcontrol

import com.cout970.glutilities.device.Keyboard
import com.cout970.matrix.extensions.times
import com.cout970.modeler.modelcontrol.action.ActionChangeSelection
import com.cout970.modeler.render.renderer.ModelRenderer
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.util.toJOML
import com.cout970.modeler.util.toJoml3d
import com.cout970.raytrace.Ray
import com.cout970.raytrace.RayTraceResult
import com.cout970.raytrace.RayTraceUtil
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.*
import org.joml.Vector3d

/**
 * Created by cout970 on 2016/12/07.
 */
class SelectionManager(val modelController: ModelController) {

    var selectionMode: SelectionMode = SelectionMode.GROUP
    var selection: Selection = SelectionNone

    fun mouseTrySelect(mouse: IVector2, renderer: ModelRenderer, viewport: IVector2) {
        val matrixMVP = renderer.matrixP * renderer.matrixV * renderer.matrixM

        val m = matrixMVP.toJOML()
        val a = m.unproject(vec3Of(mouse.x, viewport.yd - mouse.yd, 0.0).toJoml3d(), intArrayOf(0, 0, viewport.xi, viewport.yi), Vector3d()).toIVector()
        val b = m.unproject(vec3Of(mouse.x, viewport.yd - mouse.yd, 1.0).toJoml3d(), intArrayOf(0, 0, viewport.xi, viewport.yi), Vector3d()).toIVector()

        val ray = Ray(a, b)

        val hits = mutableListOf<Pair<RayTraceResult, ModelPath>>()

        if (selectionMode == SelectionMode.GROUP) {
            for (obj in modelController.model.objects) {
                for (group in obj.groups) {
                    for (i in group.components) {
                        i.rayTrace(ray)?.let {
                            hits += it to ModelPath(modelController.model, obj, group)
                        }
                    }
                }
            }
        } else if (selectionMode == SelectionMode.COMPONENT) {
            for (obj in modelController.model.objects) {
                for (group in obj.groups) {
                    for (i in group.components) {
                        i.rayTrace(ray)?.let {
                            hits += it to ModelPath(modelController.model, obj, group, i)
                        }
                    }
                }
            }
        } else if (selectionMode == SelectionMode.QUAD) {
            for (obj in modelController.model.objects) {
                for (group in obj.groups) {
                    for (i in group.components) {
                        for (quad in i.getQuads()) {
                            RayTraceUtil.rayTraceQuad(ray, i, quad.a.pos, quad.b.pos, quad.c.pos, quad.d.pos)?.let {
                                hits += it to ModelPath(modelController.model, obj, group, i, quad)
                            }
                        }
                    }
                }
            }
        } else if (selectionMode == SelectionMode.VERTEX) {
            for (obj in modelController.model.objects) {
                for (group in obj.groups) {
                    for (i in group.components) {
                        for (quad in i.getQuads()) {
                            for (v in quad.vertex) {
                                val f = v.pos - vec3Of(0.125)
                                val e = v.pos + vec3Of(0.125)
                                RayTraceUtil.rayTraceBox3(f, e, ray, i)?.let {
                                    hits += it to ModelPath(modelController.model, obj, group, i, quad, v)
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
            val sel = handleSelection(hit.second)
            modelController.historyRecord.doAction(ActionChangeSelection(selection, sel, this) { renderer.cache.clear() })
        } else {
            if (!modelController.eventController.keyboard.isKeyPressed(Keyboard.KEY_LEFT_CONTROL)) {
                modelController.historyRecord.doAction(ActionChangeSelection(selection, SelectionNone, this) { renderer.cache.clear() })
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
}