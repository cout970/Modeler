package com.cout970.modeler.gui.canvas.cursor

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.selection.*
import com.cout970.modeler.controller.ITaskProcessor
import com.cout970.modeler.core.helpers.PickupHelper
import com.cout970.modeler.core.log.Profiler
import com.cout970.modeler.core.model.edges
import com.cout970.modeler.core.model.faces
import com.cout970.modeler.core.model.getSelectedObjects
import com.cout970.modeler.core.model.pos
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.canvas.Canvas
import com.cout970.modeler.gui.canvas.ISelectable
import com.cout970.modeler.gui.canvas.input.DraggingCursor
import com.cout970.modeler.gui.canvas.tool.DragHandler
import com.cout970.modeler.gui.canvas.tool.DragListener
import com.cout970.modeler.util.hasNaN
import com.cout970.modeler.util.middle
import com.cout970.vector.extensions.toVector3

class CursorManager {

    lateinit var taskProcessor: ITaskProcessor
    lateinit var updateCanvas: () -> Unit
    private lateinit var gui: Gui

    var modelCursor: Cursor? = null
    var textureCursor: Cursor? = null

    val cursorDrag = DraggingCursor()

    // debug
    lateinit var handler: DragHandler

    fun setGui(gui: Gui) {
        this.gui = gui
        handler = DragHandler(DragListener(gui))
    }

    fun tick() {
        Profiler.startSection("cursorManager")

        handler.tick(gui)

        if (!handler.isDragging()) {
            updateCanvas()
        }

        Profiler.endSection()
    }

    private fun processCursor(canvas: Canvas): Boolean {
        val cursor: Cursor
        val targets: List<ISelectable>

        val textureMode = canvas.viewMode == SelectionTarget.TEXTURE
        gui.state.hoveredObject = null

        if (textureMode) {
            cursor = textureCursor ?: return true
            targets = cursor.getSelectablePartsTexture(gui, canvas)
        } else {
            cursor = modelCursor ?: return true
            targets = cursor.getSelectablePartsModel(gui, canvas)
        }

        cursorDrag.tick(gui, canvas, targets, cursor, textureMode)

        gui.state.hoveredObject = cursorDrag.hovered

        if (canvas.viewMode == SelectionTarget.TEXTURE) {
            textureCursor = cursorDrag.currentCursor
        } else {
            modelCursor = cursorDrag.currentCursor


            val newCursor = cursorDrag.currentCursor

            if (newCursor == null) {
                gui.state.cursor.visible = false
            } else {
                gui.state.cursor.visible = true
                gui.state.cursor.position = newCursor.center
            }
        }

        gui.state.run {
            val model = cursorDrag.modelCache?.model
            tmpModel = model

            model ?: return@run
            modelHash = model.hashCode()
        }

        cursorDrag.taskToPerform?.let { task ->
            taskProcessor.processTask(task)
            cursorDrag.taskToPerform = null
        }

        return !cursorDrag.isDragging()
    }

    fun updateCursors(gui: Gui) {
        val model = gui.state.tmpModel ?: gui.programState.model
        val material = gui.state.selectedMaterial
        val texSel = gui.programState.textureSelectionHandler.getSelection()
        val modSel = gui.programState.modelSelectionHandler.getSelection()
        modelCursor = modSel.map { getModelCursor(model, it) }.getOrNull()
        textureCursor = texSel.map { getTextureCursor(model, it, material) }.getOrNull()
    }

    fun getTextureCursor(model: IModel, selection: ISelection, materialRef: IMaterialRef): Cursor? {
        val material = model.getMaterial(materialRef)
        return when (selection.selectionType) {
            SelectionType.OBJECT -> model.getSelectedObjects(selection)
                    .map { it.mesh.tex.middle() }
                    .middle()

            SelectionType.FACE -> selection.refs
                    .filterIsInstance<IFaceRef>()
                    .map { model.getObject(it.toObjectRef()) to it.faceIndex }
                    .groupBy { it.first }
                    .map { it.key to it.value.map { it.second } }
                    .map { (obj, faces) ->
                        faces.map { obj.mesh.faces[it] }
                                .flatMap { it.tex }
                                .mapNotNull { obj.mesh.tex.getOrNull(it) }
                                .middle()
                    }
                    .middle()

            SelectionType.EDGE -> selection.refs
                    .filterIsInstance<IEdgeRef>()
                    .map { model.getObject(it.toObjectRef()) to it }
                    .flatMap { (obj, ref) -> listOf(obj.mesh.tex[ref.firstIndex], obj.mesh.tex[ref.secondIndex]) }
                    .middle()

            SelectionType.VERTEX -> selection.refs
                    .filterIsInstance<IPosRef>()
                    .map { model.getObject(it.toObjectRef()).mesh.tex[it.posIndex] }
                    .middle()

        }.let { middle ->
            if (!middle.hasNaN()) {
                val center = PickupHelper.fromMaterialToCanvas(middle, material)
                Cursor(center.toVector3(0.0))
            } else null
        }
    }

    fun getModelCursor(model: IModel, selection: ISelection): Cursor? {
        return when (selection.selectionType) {
            SelectionType.OBJECT -> model.getSelectedObjects(selection)
                    .map { it.getCenter() }
                    .middle()

            SelectionType.FACE -> selection.faces
                    .map { model.getObject(it.toObjectRef()) to it.faceIndex }
                    .map { (obj, index) ->
                        obj.mesh.faces[index]
                                .pos
                                .mapNotNull { obj.mesh.pos.getOrNull(it) }
                                .middle()
                    }
                    .middle()

            SelectionType.EDGE -> selection.edges
                    .map { model.getObject(it.toObjectRef()) to it }
                    .flatMap { (obj, ref) -> listOf(obj.mesh.pos[ref.firstIndex], obj.mesh.pos[ref.secondIndex]) }
                    .middle()

            SelectionType.VERTEX -> selection.pos
                    .map { model.getObject(it.toObjectRef()).mesh.pos[it.posIndex] }
                    .middle()

        }.let { middle ->
            if (!middle.hasNaN()) Cursor(middle) else null
        }
    }
}