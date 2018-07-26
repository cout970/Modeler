package com.cout970.modeler.gui.canvas.input

/**
 * Created by cout970 on 2017/08/16.
 */

// TODO remove
//data class ModelCache(val model: IModel, val offset: Float = 0f)
//
//class DraggingCursor {
//    var hovered: ISelectable? = null
//    var startMousePos: IVector2? = null
//    var endMousePos: IVector2? = null
//    var modelCache: ModelCache? = null
//    var startCursor: CursorableLinkedList.Cursor? = null
//    var currentCursor: CursorableLinkedList.Cursor? = null
//    var taskToPerform: ITask? = null
//
//    fun tick(gui: Gui, canvas: Canvas, targets: List<ISelectable>, cursor: CursorableLinkedList.Cursor, texture: Boolean) {
//        val mouse = MouseState.from(gui)
//        currentCursor = cursor
//
//        when {
//            !isDragging() && mouse.mousePress -> {
//                startDrag(mouse, cursor)
//            }
//            isDragging() && !mouse.mousePress -> {
//                endDrag(gui)
//            }
//        }
//        when {
//            isDragging() -> { // while dragging
//                endMousePos = mouse.mousePos
//                hovered?.let { hovered ->
//                    val travel = startMousePos!! to endMousePos!!
//                    val model = gui.programState.model
//                    val material = model.getMaterial(gui.state.selectedMaterial)
//                    val selection = if (canvas.viewMode == SelectionTarget.TEXTURE) {
//                        gui.programState.textureSelectionHandler.getSelection()
//                    } else {
//                        gui.programState.modelSelectionHandler.getSelection()
//                    }
//                    applyTransformation(gui, selection, hovered, travel, canvas, material)
//                }
//                (hovered as? ITranslatable)?.let {
//                    val offset = modelCache?.offset ?: return@let
//                    val center = startCursor?.center ?: return@let
//                    currentCursor = CursorableLinkedList.Cursor(center + it.translationAxis * offset)
//                }
//            }
//            !isDragging() -> {
//                if (texture) {
//                    val clickPos = PickupHelper.getMousePosAbsolute(canvas, mouse.mousePos)
//                    val model = gui.programState.model
//                    val material = model.getMaterial(gui.state.selectedMaterial)
//                    this.hovered = Hover.getHoveredObject(clickPos, material, targets)
//                } else {
//                    val context = CanvasHelper.getMouseSpaceContext(canvas, mouse.mousePos)
//                    this.hovered = Hover.getHoveredObject(context, targets)
//                }
//            }
//        }
//    }
//
//    fun isDragging() = startMousePos != null
//
//    fun startDrag(mouse: MouseState, cursor: Cursor) {
//        startCursor = cursor
//        startMousePos = mouse.mousePos
//        endMousePos = mouse.mousePos
//    }
//
//    fun endDrag(gui: Gui) {
//        gui.state.run {
//            tmpModel = null
//            modelCache?.let { cache ->
//                val oldModel = gui.programState.model
//                taskToPerform = TaskUpdateModel(oldModel = oldModel, newModel = cache.model)
//            }
//        }
//        modelCache = null
//        startCursor = null
//        endMousePos = null
//        startMousePos = null
//    }
//
//    fun applyTransformation(gui: Gui, selection: Nullable<ISelection>, hovered: ISelectable,
//                            pos: Pair<IVector2, IVector2>,
//                            canvas: Canvas, mat: IMaterial) {
//
//        val mode = gui.state.transformationMode
//        val oldModel = gui.programState.model
//        val modelCache = this.modelCache ?: ModelCache(oldModel)
//
//        val newOffset = when {
//            hovered is ITranslatable && mode == TransformationMode.TRANSLATION -> {
//                getTranslationOffset(hovered, canvas, pos, gui.input)
//            }
//            hovered is IRotable && mode == TransformationMode.ROTATION -> {
//                RotationHelper.getOffsetGlobal(hovered, canvas, pos, gui.input)
//            }
//            hovered is IScalable && mode == TransformationMode.SCALE -> {
//                getScaleOffset(hovered, canvas, pos, gui.input)
//            }
//            else -> 0f
//        }
//
//        if (newOffset != modelCache.offset) {
//
//            selection.ifNotNull { sel ->
//                val part = oldModel to sel
//
//                val model = when {
//                    hovered is ITranslatable && mode == TransformationMode.TRANSLATION -> {
//                        applyTranslationOffset(hovered, part, newOffset, mat)
//                    }
//                    hovered is IRotable && mode == TransformationMode.ROTATION -> {
//                        applyRotationOffset(hovered, part, newOffset, mat)
//                    }
//                    hovered is IScalable && mode == TransformationMode.SCALE -> {
//                        applyScaleOffset(hovered, part, newOffset, mat)
//                    }
//                    else -> null
//                }
//
//                model?.let { this.modelCache = ModelCache(it, newOffset) }
//            }
//        }
//    }
//
//    private fun getScaleOffset(obj: IScalable, canvas: Canvas, pos: Pair<IVector2, IVector2>, input: IInput): Float {
//        val context = CanvasHelper.getContext(canvas, pos)
//
//        return ScaleHelper.getOffset(
//                obj = obj,
//                canvas = canvas,
//                input = input,
//                newContext = context.first,
//                oldContext = context.second
//        )
//    }
//
//    private fun applyScaleOffset(obj: IScalable, part: Pair<IModel, ISelection>, offset: Float,
//                                 mat: IMaterial): IModel =
//            obj.applyScale(offset, part.second, part.first, mat)
//
//
//    private fun applyRotationOffset(obj: IRotable, part: Pair<IModel, ISelection>, offset: Float,
//                                    mat: IMaterial): IModel =
//            obj.applyRotation(offset, part.second, part.first, mat)
//
//
//    private fun getTranslationOffset(obj: ITranslatable, canvas: Canvas, pos: Pair<IVector2, IVector2>,
//                                     input: IInput): Float {
//
//        val context = CanvasHelper.getContext(canvas, pos)
//
//        return TranslationHelper.getOffset(
//                obj = obj,
//                canvas = canvas,
//                input = input,
//                newContext = context.first,
//                oldContext = context.second
//        )
//    }
//
//    private fun applyTranslationOffset(obj: ITranslatable, part: Pair<IModel, ISelection>, offset: Float,
//                                       mat: IMaterial): IModel =
//            obj.applyTranslation(offset, part.second, part.first, mat)
//}