package com.cout970.modeler.gui.canvas.cursor

import com.cout970.modeler.controller.ITaskProcessor
import com.cout970.modeler.core.log.Profiler
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.canvas.tool.DragHandler
import com.cout970.modeler.gui.canvas.tool.DragListener2D
import com.cout970.modeler.gui.canvas.tool.DragListener3D
import com.cout970.modeler.gui.canvas.tool.DragListenerCombinator

class CursorManager {

    lateinit var taskProcessor: ITaskProcessor
    lateinit var updateCanvas: () -> Unit
    private lateinit var gui: Gui


    // debug
    lateinit var handler: DragHandler

    fun setGui(gui: Gui) {
        this.gui = gui
        handler = DragHandler(DragListenerCombinator(DragListener3D(gui), DragListener2D(gui)))
    }

    fun tick() {
        Profiler.startSection("cursorManager")

        handler.tick(gui)

        if (!handler.isDragging()) {
            updateCanvas()
        }

        Profiler.endSection()
    }

//    private fun processCursor(canvas: Canvas): Boolean {
//        val cursor: CursorableLinkedList.Cursor
//        val targets: List<ISelectable>
//
//        val textureMode = canvas.viewMode == SelectionTarget.TEXTURE
//        gui.state.hoveredObject = null
//
//        if (textureMode) {
//            cursor = textureCursor ?: return true
//            targets = cursor.getSelectablePartsTexture(gui, canvas)
//        } else {
//            cursor = modelCursor ?: return true
//            targets = cursor.getSelectablePartsModel(gui, canvas)
//        }
//
//        cursorDrag.tick(gui, canvas, targets, cursor, textureMode)
//
//        gui.state.hoveredObject = cursorDrag.hovered
//
//        if (canvas.viewMode == SelectionTarget.TEXTURE) {
//            textureCursor = cursorDrag.currentCursor
//        } else {
//            modelCursor = cursorDrag.currentCursor
//        }
//
//        gui.state.run {
//            val model = cursorDrag.modelCache?.model
//            tmpModel = model
//
//            model ?: return@run
//            modelHash = model.hashCode()
//        }
//
//        cursorDrag.taskToPerform?.let { task ->
//            taskProcessor.processTask(task)
//            cursorDrag.taskToPerform = null
//        }
//
//        return !cursorDrag.isDragging()
//    }

//    fun updateCursors(gui: Gui) {
//        val model = gui.state.tmpModel ?: gui.programState.model
//        val material = gui.state.selectedMaterial
//        val texSel = gui.programState.textureSelectionHandler.getSelection()
//        val modSel = gui.programState.modelSelectionHandler.getSelection()
//        modelCursor = modSel.map { getModelCursor(model, it) }.getOrNull()
//        textureCursor = texSel.map { getTextureCursor(model, it, material) }.getOrNull()
//    }


//    fun getModelCursor(model: IModel, selection: ISelection): Cursor? {
//        return when (selection.selectionType) {
//            SelectionType.OBJECT -> model.getSelectedObjects(selection)
//                    .map { it.getCenter() }
//                    .middle()
//
//            SelectionType.FACE -> selection.faces
//                    .map { model.getObject(it.toObjectRef()) to it.faceIndex }
//                    .map { (obj, index) ->
//                        obj.mesh.faces[index]
//                                .pos
//                                .mapNotNull { obj.mesh.pos.getOrNull(it) }
//                                .middle()
//                    }
//                    .middle()
//
//            SelectionType.EDGE -> selection.edges
//                    .map { model.getObject(it.toObjectRef()) to it }
//                    .flatMap { (obj, ref) -> listOf(obj.mesh.pos[ref.firstIndex], obj.mesh.pos[ref.secondIndex]) }
//                    .middle()
//
//            SelectionType.VERTEX -> selection.pos
//                    .map { model.getObject(it.toObjectRef()).mesh.pos[it.posIndex] }
//                    .middle()
//
//        }.let { middle ->
//            if (!middle.hasNaN()) Cursor(middle) else null
//        }
//        return null
//    }
}