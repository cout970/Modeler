package com.cout970.modeler.gui.canvas

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.*
import com.cout970.modeler.core.model.getSelectedObjects
import com.cout970.modeler.core.model.selection.ObjectRef
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.canvas.cursor.Cursor
import com.cout970.modeler.util.hasNaN
import com.cout970.modeler.util.middle
import com.cout970.vector.extensions.toVector3

class CursorManager {

    var modelCursor: Cursor? = null
    var textureCursor: Cursor? = null

    fun updateCursors(gui: Gui) {
        val model = gui.state.tmpModel ?: gui.modelAccessor.model
        val texSel = gui.modelAccessor.textureSelectionHandler.getSelection()
        val modSel = gui.modelAccessor.modelSelectionHandler.getSelection()
        modelCursor = modSel.map { getModelCursor(model, it) }.getOrNull()
        textureCursor = texSel.map { getTextureCursor(model, it) }.getOrNull()
    }

    fun getTextureCursor(model: IModel, selection: ISelection): Cursor? {
        return when (selection.selectionType) {
            SelectionType.OBJECT -> model.getSelectedObjects(selection)
                    .map { it.mesh.tex.middle() }
                    .middle()

            SelectionType.FACE -> selection.refs
                    .filterIsInstance<IFaceRef>()
                    .map { model.getObject(ObjectRef(it.objectIndex)) to it.faceIndex }
                    .map { (obj, index) ->
                        obj.mesh.faces[index]
                                .pos
                                .mapNotNull { obj.mesh.tex.getOrNull(it) }
                                .middle()
                    } // TODO fix java.lang.IndexOutOfBoundsException: Index: 32, Size: 32
                    .middle()

            SelectionType.EDGE -> selection.refs
                    .filterIsInstance<IEdgeRef>()
                    .map { model.getObject(ObjectRef(it.objectIndex)) to it }
                    .flatMap { (obj, ref) -> listOf(obj.mesh.tex[ref.firstIndex], obj.mesh.tex[ref.secondIndex]) }
                    .middle()

            SelectionType.VERTEX -> selection.refs
                    .filterIsInstance<IPosRef>()
                    .map { model.getObject(ObjectRef(it.objectIndex)).mesh.tex[it.posIndex] }
                    .middle()

        }.let { middle ->
            if (!middle.hasNaN()) Cursor(middle.toVector3(0.0)) else null
        }
    }

    fun getModelCursor(model: IModel, selection: ISelection): Cursor? {
        return when (selection.selectionType) {
            SelectionType.OBJECT -> model.getSelectedObjects(selection)
                    .map { it.getCenter() }
                    .middle()

            SelectionType.FACE -> selection.refs
                    .filterIsInstance<IFaceRef>()
                    .map { model.getObject(ObjectRef(it.objectIndex)) to it.faceIndex }
                    .map { (obj, index) ->
                        obj.mesh.faces[index]
                                .pos
                                .mapNotNull { obj.mesh.pos.getOrNull(it) }
                                .middle()
                    } // TODO fix java.lang.IndexOutOfBoundsException: Index: 32, Size: 32
                    .middle()

            SelectionType.EDGE -> selection.refs
                    .filterIsInstance<IEdgeRef>()
                    .map { model.getObject(ObjectRef(it.objectIndex)) to it }
                    .flatMap { (obj, ref) -> listOf(obj.mesh.pos[ref.firstIndex], obj.mesh.pos[ref.secondIndex]) }
                    .middle()

            SelectionType.VERTEX -> selection.refs
                    .filterIsInstance<IPosRef>()
                    .map { model.getObject(ObjectRef(it.objectIndex)).mesh.pos[it.posIndex] }
                    .middle()

        }.let { middle ->
            if (!middle.hasNaN()) Cursor(middle) else null
        }
    }
}