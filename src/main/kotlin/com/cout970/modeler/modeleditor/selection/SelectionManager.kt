package com.cout970.modeler.modeleditor.selection

import com.cout970.modeler.model.Model
import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.modeleditor.action.ActionChangeModelSelection
import com.cout970.modeler.selection.*
import com.cout970.modeler.util.Raytracer
import com.cout970.modeler.util.Raytracer.raytraceEdgePos
import com.cout970.modeler.util.Raytracer.raytraceElements
import com.cout970.modeler.util.Raytracer.raytraceQuadPos
import com.cout970.modeler.util.Raytracer.raytraceVertexPos
import com.cout970.raytrace.Ray
import com.cout970.raytrace.RayTraceResult
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2016/12/07.
 */
class SelectionManager(val modelEditor: ModelEditor) {

    var selectionMode: SelectionMode = SelectionMode.ELEMENT

    var elementSelection: ElementSelection = ElementSelection.EMPTY
    var vertexPosSelection: VertexPosSelection = VertexPosSelection.EMPTY
    var vertexTexSelection: VertexTexSelection = VertexTexSelection.EMPTY

    var vertexPosTarget: SelectionTarget = SelectionTarget.QUAD
    var vertexTexTarget: SelectionTarget = SelectionTarget.QUAD

    fun clearSelection() {
        changeSelection(ElementSelection.EMPTY, VertexPosSelection.EMPTY, VertexTexSelection.EMPTY)
    }

    fun changeSelection(elementSelection: ElementSelection = this.elementSelection,
                        vertexPosSelection: VertexPosSelection = this.vertexPosSelection,
                        vertexTexSelection: VertexTexSelection = this.vertexTexSelection) {

        val action = ActionChangeModelSelection(
                elementSelection, vertexPosSelection, vertexTexSelection,
                this.elementSelection, this.vertexPosSelection, this.vertexTexSelection,
                modelEditor)

        modelEditor.historyRecord.doAction(action)
    }

    fun selectPos(ray: Ray, zoom: Float, allowMultiSelection: Boolean) {
        if (selectionMode == SelectionMode.ELEMENT) {
            val result = raytraceElements(ray, modelEditor.model)

            if (result == null && allowMultiSelection) {
                //ignore
            } else if (result == null) {
                changeSelection(elementSelection = ElementSelection.EMPTY)
            } else if (allowMultiSelection) {
                val path = result.second
                if (elementSelection.isSelected(path)) {
                    changeSelection(elementSelection = ElementSelection(elementSelection.paths - path))
                } else {
                    changeSelection(elementSelection = ElementSelection(elementSelection.paths + path))
                }
            } else {
                val path = result.second
                changeSelection(elementSelection = ElementSelection(listOf(path)))
            }

        } else if (vertexPosTarget == SelectionTarget.VERTEX) {
            val result = raytraceVertexPos(ray, modelEditor.model, zoom / 10)

            if (result == null && allowMultiSelection) {
                //ignore
            } else if (result == null) {
                changeSelection(vertexPosSelection = VertexPosSelection.EMPTY)
            } else if (allowMultiSelection) {
                val path = result.second
                if (vertexPosSelection.isSelected(path)) {
                    val newPaths = vertexPosSelection.pathList - path
                    if (newPaths.isNotEmpty()) {
                        changeSelection(vertexPosSelection = VertexPosSelection.of(newPaths))
                    } else {
                        changeSelection(vertexPosSelection = VertexPosSelection.EMPTY)
                    }
                } else {
                    changeSelection(vertexPosSelection = VertexPosSelection.of(vertexPosSelection.pathList + path))
                }
            } else {
                val path = result.second
                changeSelection(vertexPosSelection = VertexPosSelection.of(listOf(path)))
            }

        } else if (vertexPosTarget == SelectionTarget.EDGE) {
            val result = raytraceEdgePos(ray, modelEditor.model, zoom / 10)

            if (result == null && allowMultiSelection) {
                //ignore
            } else if (result == null) {
                changeSelection(vertexPosSelection = VertexPosSelection.EMPTY)
            } else if (allowMultiSelection) {
                val paths = result.second.toList()
                if (paths.all { vertexPosSelection.isSelected(it) }) {
                    val newPaths = vertexPosSelection.pathList - paths
                    if (newPaths.isNotEmpty()) {
                        changeSelection(vertexPosSelection = VertexPosSelection.of(newPaths))
                    } else {
                        changeSelection(vertexPosSelection = VertexPosSelection.EMPTY)
                    }
                } else {
                    changeSelection(vertexPosSelection = VertexPosSelection.of(vertexPosSelection.pathList + paths))
                }
            } else {
                val paths = result.second.toList()
                changeSelection(vertexPosSelection = VertexPosSelection.of(paths))
            }

        } else if (vertexPosTarget == SelectionTarget.QUAD) {
            val result = raytraceQuadPos(ray, modelEditor.model)

            if (result == null && allowMultiSelection) {
                //ignore
            } else if (result == null) {
                changeSelection(vertexPosSelection = VertexPosSelection.EMPTY)
            } else if (allowMultiSelection) {
                val paths = result.second
                if (paths.all { vertexPosSelection.isSelected(it) }) {
                    val newPaths = vertexPosSelection.pathList - paths
                    if (newPaths.isNotEmpty()) {
                        changeSelection(vertexPosSelection = VertexPosSelection.of(newPaths))
                    } else {
                        changeSelection(vertexPosSelection = VertexPosSelection.EMPTY)
                    }
                } else {
                    changeSelection(vertexPosSelection = VertexPosSelection.of(vertexPosSelection.pathList + paths))
                }
            } else {
                val paths = result.second
                changeSelection(vertexPosSelection = VertexPosSelection.of(paths))
            }
        }
    }

    fun selectTex(ray: Ray, zoom: Float, allowMultiSelection: Boolean, to3D: (IVector2) -> IVector3) {
        if (vertexTexTarget == SelectionTarget.QUAD) {
            val result = Raytracer.raytraceQuadTex(ray, modelEditor.model, to3D)

            if (result == null && allowMultiSelection) {
                //ignore
            } else if (result == null) {
                changeSelection(vertexTexSelection = VertexTexSelection.EMPTY)
            } else if (allowMultiSelection) {
                val paths = result.second
                if (paths.all { vertexTexSelection.isSelected(it) }) {
                    val newPaths = vertexTexSelection.pathList - paths
                    if (newPaths.isNotEmpty()) {
                        changeSelection(vertexTexSelection = VertexTexSelection.of(newPaths))
                    } else {
                        changeSelection(vertexTexSelection = VertexTexSelection.EMPTY)
                    }
                } else {
                    changeSelection(vertexTexSelection = VertexTexSelection.of(vertexTexSelection.pathList + paths))
                }
            } else {
                val paths = result.second
                changeSelection(vertexTexSelection = VertexTexSelection.of(paths))
            }
        }
    }

    fun getMouseHit(ray: Ray, model: Model = modelEditor.model): RayTraceResult? {
        return raytraceElements(ray, model)?.first
    }
}