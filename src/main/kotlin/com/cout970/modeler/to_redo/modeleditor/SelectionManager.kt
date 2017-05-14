package com.cout970.modeler.to_redo.modeleditor

import com.cout970.modeler.core.record.action.ActionChangeSelection
import com.cout970.modeler.to_redo.model.Model
import com.cout970.modeler.to_redo.selection.*
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

    var selectionState = SelectionState(ElementSelection.EMPTY, VertexPosSelection.EMPTY, VertexTexSelection.EMPTY)
        set(value) {
            listeners.forEach { it(field, value) }
            field = value
        }

    var vertexPosTarget: SelectionTarget = SelectionTarget.QUAD
    var vertexTexTarget: SelectionTarget = SelectionTarget.QUAD

    val listeners = mutableListOf<(SelectionState, SelectionState) -> Unit>()

    fun getSelectionCenter(model: Model): IVector3 {
        return if (selectionMode == SelectionMode.EDIT) {
            vertexPosSelection.center3D(model)
        } else {
            elementSelection.getSelectedVertexPos(model).center3D(model)
        }
    }

    fun hasNoSelection(): Boolean {
        return if (selectionMode == SelectionMode.EDIT) {
            vertexPosSelection == VertexPosSelection.EMPTY
        } else {
            elementSelection == ElementSelection.EMPTY
        }
    }

    fun clearSelection() {
        changeSelection(ElementSelection.EMPTY, VertexPosSelection.EMPTY, VertexTexSelection.EMPTY)
    }

    fun changeSelection(elementSelection: ElementSelection = selectionState.element,
                        vertexPosSelection: VertexPosSelection = selectionState.pos,
                        vertexTexSelection: VertexTexSelection = selectionState.tex) {

        val action = ActionChangeSelection(selectionState,
                SelectionState(elementSelection, vertexPosSelection, vertexTexSelection),
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
                        changeSelection(vertexPosSelection = VertexPosSelection.ofVertex(newPaths))
                    } else {
                        changeSelection(vertexPosSelection = VertexPosSelection.EMPTY)
                    }
                } else {
                    changeSelection(
                            vertexPosSelection = VertexPosSelection.ofVertex(vertexPosSelection.pathList + path))
                }
            } else {
                val path = result.second
                changeSelection(vertexPosSelection = VertexPosSelection.ofVertex(listOf(path)))
            }

        } else if (vertexPosTarget == SelectionTarget.EDGE) {
            val result = raytraceEdgePos(ray, modelEditor.model, zoom / 10)

            if (result == null && allowMultiSelection) {
                //ignore
            } else if (result == null) {
                changeSelection(vertexPosSelection = VertexPosSelection.EMPTY)
            } else if (allowMultiSelection) {
                val path = result.second

                if (vertexPosSelection.isSelected(path)) {
                    val newPaths = vertexPosSelection.subPathHandler.toEdgePaths() - path
                    if (newPaths.isNotEmpty()) {
                        changeSelection(vertexPosSelection = VertexPosSelection.ofEdges(newPaths))
                    } else {
                        changeSelection(vertexPosSelection = VertexPosSelection.EMPTY)
                    }
                } else {
                    changeSelection(
                            vertexPosSelection = VertexPosSelection.ofEdges(vertexPosSelection.toEdgePaths() + path))
                }
            } else {
                val path = result.second

                changeSelection(vertexPosSelection = VertexPosSelection.ofEdges(listOf(path)))
            }

        } else if (vertexPosTarget == SelectionTarget.QUAD) {
            val result = raytraceQuadPos(ray, modelEditor.model)

            if (result == null && allowMultiSelection) {
                //ignore
            } else if (result == null) {
                changeSelection(vertexPosSelection = VertexPosSelection.EMPTY)
            } else if (allowMultiSelection) {
                val path = result.second
                if (vertexPosSelection.isSelected(path)) {
                    val newPaths = vertexPosSelection.toFacePaths() - path
                    if (newPaths.isNotEmpty()) {
                        changeSelection(vertexPosSelection = VertexPosSelection.ofFaces(newPaths))
                    } else {
                        changeSelection(vertexPosSelection = VertexPosSelection.EMPTY)
                    }
                } else {
                    changeSelection(
                            vertexPosSelection = VertexPosSelection.ofFaces(vertexPosSelection.toFacePaths() + path))
                }
            } else {
                val path = result.second
                changeSelection(vertexPosSelection = VertexPosSelection.ofFaces(listOf(path)))
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
                val path = result.second
                if (vertexTexSelection.isSelected(path)) {
                    val newPaths = vertexTexSelection.toFacePaths() - path
                    if (newPaths.isNotEmpty()) {
                        changeSelection(vertexTexSelection = VertexTexSelection.ofFaces(newPaths))
                    } else {
                        changeSelection(vertexTexSelection = VertexTexSelection.EMPTY)
                    }
                } else {
                    changeSelection(
                            vertexTexSelection = VertexTexSelection.ofFaces(vertexTexSelection.toFacePaths() + path))
                }
            } else {
                val path = result.second
                changeSelection(vertexTexSelection = VertexTexSelection.ofFaces(listOf(path)))
            }
        }
    }

    fun getMouseHit(ray: Ray, model: Model = modelEditor.model): RayTraceResult? {
        return raytraceElements(ray, model)?.first
    }

    fun getSelectedVertexPos(model: Model): VertexPosSelection {
        if (selectionMode == SelectionMode.EDIT) {
            return vertexPosSelection
        } else {
            return elementSelection.getSelectedVertexPos(model)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SelectionManager) return false

        if (selectionMode != other.selectionMode) return false
        if (selectionState != other.selectionState) return false
        if (vertexPosTarget != other.vertexPosTarget) return false
        if (vertexTexTarget != other.vertexTexTarget) return false

        return true
    }

    override fun hashCode(): Int {
        var result = selectionMode.hashCode()
        result = 31 * result + selectionState.hashCode()
        result = 31 * result + vertexPosTarget.hashCode()
        result = 31 * result + vertexTexTarget.hashCode()
        return result
    }
}