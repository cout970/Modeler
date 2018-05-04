package com.cout970.modeler.gui.rcomponents.right

import com.cout970.glutilities.device.Mouse
import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.*
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.controller.Dispatcher
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.model.ref
import com.cout970.modeler.core.model.selection.ObjectRefNone
import com.cout970.modeler.core.project.IModelAccessor
import com.cout970.modeler.gui.event.EventModelUpdate
import com.cout970.modeler.gui.event.EventSelectionUpdate
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.input.event.IInput
import com.cout970.modeler.util.getOr
import com.cout970.modeler.util.toColor
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.util.toJoml2f
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RComponent
import com.cout970.reactive.core.RProps
import com.cout970.reactive.core.RState
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.*
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.Vector2
import org.joml.Vector2i
import org.liquidengine.legui.animation.Animation
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Panel
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.style.border.SimpleLineBorder
import org.liquidengine.legui.style.color.ColorConstants
import kotlin.math.min

data class Slot(val obj: IObjectRef?, val group: IGroupRef?, val level: Int)

data class ModelTreeProps(val modelAccessor: IModelAccessor, val input: IInput, val dispatcher: Dispatcher) : RProps
data class ModelTreeState(val selectedObj: IObjectRef) : RState


class ModelTree : RComponent<ModelTreeProps, ModelTreeState>() {

    var animation: Animation? = null

    override fun getInitialState() = ModelTreeState(ObjectRefNone)

    fun generateObjectMap(): List<Slot> {

        val model = props.modelAccessor.model
        val tree = model.groupTree
        val map = mutableListOf<Slot>()

        tree.getChildren(RootGroupRef).forEach {
            addGroupAndChildren(model, tree, it, map, 0)
        }

        model.getGroupObjects(RootGroupRef).forEach { ref ->
            map += Slot(ref, null, 0)
        }


        return map
    }

    fun addGroupAndChildren(model: IModel, tree: IGroupTree, group: IGroupRef, map: MutableList<Slot>, level: Int) {

        map += Slot(null, group, level)

        if (!model.getGroup(group).visible) return

        model.getGroupObjects(group).forEach { ref ->
            map += Slot(ref, null, min(5, level + 1))
        }

        tree.getChildren(group).forEach {
            addGroupAndChildren(model, tree, it, map, min(5, level + 1))
        }
    }

    override fun RBuilder.render() = div("ModelTree") {

        style {
            transparent()
            border(2f) { greyColor }
            rectCorners()
            height = 300f
            posY = 70f
        }

        postMount {
            marginX(5f)
            height = (parent.height - 70f) / 2f
        }

        on<EventModelUpdate> { rerender() }
        on<EventSelectionUpdate> { rerender() }

        comp(FixedLabel()) {
            style {
                textState.apply {
                    this.text = "Model Tree"
                    textColor = Config.colorPalette.textColor.toColor()
                    horizontalAlign = HorizontalAlign.CENTER
                    fontSize = 20f
                }
            }

            postMount {
                posX = 50f
                posY = 0f
                sizeX = parent.sizeX - 100f
                sizeY = 24f
            }
        }

        scrollablePanel("ModeTreeScrollPanel") {

            style {
                transparent()
                borderless()
            }

            postMount {
                posX = 5f
                posY = 24f + 5f
                sizeX = parent.sizeX - 10f
                sizeY = parent.sizeY - posY - 5f
            }

            horizontalScroll {
                style { hide() }
            }

            verticalScroll {
                style {
                    rectCorners()
                    style.minWidth = 16f
                    arrowColor = color { lightBrightColor }
                    scrollColor = color { darkColor }
                    visibleAmount = 50f
                    backgroundColor { color { lightBrightColor } }
                }
            }

            container {

                val model = props.modelAccessor.model
                val objectMap = generateObjectMap()
                val selected = props.modelAccessor.modelSelection
                        .map { sel -> { obj: IObjectRef -> sel.isSelected(obj) } }
                        .getOr { _: IObjectRef -> false }

                objectMap.forEachIndexed { index, slot ->
                    val group = slot.group
                    val obj = slot.obj

                    if (group != null) {
                        group(index, slot.level, model.getGroup(group))
                    } else if (obj != null) {
                        obj(index, slot.level, model.getObject(obj), selected(obj))
                    }
                }

                style {
                    transparent()
                    borderless()
                    height = objectMap.size * 26f
                    width = 251f
                }

                postMount {
                    animation?.stopAnimation()
                    val anim = ModelTreeAnimation(props.modelAccessor, objectMap,
                            this, props.input, this@ModelTree::rerender, props.dispatcher)

                    animation = anim.apply { startAnimation() }
                }
            }
        }
    }

    fun RBuilder.group(index: Int, level: Int, group: IGroup) {
        div(group.name) {
            style {
                sizeY = 24f
                posX = level * 24f
                posY = index * (sizeY + 2f)
                transparent()
                borderless()
                rectCorners()

                background { lightDarkColor }
            }

            postMount {
                sizeX = parent.sizeX - 5f - level * 24f
            }


            val cmd = if (group.visible) "tree.view.hide.group" else "tree.view.show.group"
            val off = level * 24f

            +IconButton(cmd, "group_icon", 0f, 0f, 24f, 24f).apply {
                metadata += "ref" to group.ref
                if (!group.visible) {
                    background { brightColor }
                }
            }

            child(ToggleName::class, ToggleName.Props(group, off, props.dispatcher))


            +IconButton("tree.view.delete.group", "deleteIcon", 222f - off, 0f, 24f, 24f).apply {
                transparent()
                borderless()
                metadata += "ref" to group.ref
                setTooltip("Delete group")
            }
        }
    }

    fun RBuilder.obj(index: Int, level: Int, obj: IObject, selected: Boolean) {
        div(obj.name) {
            style {
                sizeY = 24f
                posX = 24f * level
                posY = index * (sizeY + 2f)
                transparent()
                borderless()
                rectCorners()

                if (selected) {
                    background { lightBrightColor }
                } else {
                    background { lightDarkColor }
                }
            }

            postMount {
                sizeX = parent.sizeX - 5f - level * 24f
            }

            val icon = if (obj is IObjectCube) "obj_type_cube" else "obj_type_mesh"
            val off = level * 24f

            +IconButton("tree.view.select.item", icon, 0f, 0f, 24f, 24f).apply {
                metadata += "ref" to obj.ref
            }

            +TextButton("tree.view.select.item", obj.name, 24f, 0f, 172f - off, 24f).apply {
                transparent()
                borderless()
                fontSize = 20f
                horizontalAlign = HorizontalAlign.LEFT
                textState.padding.x = 2f
                metadata += "ref" to obj.ref
            }

            if (obj.visible) {
                +IconButton("tree.view.hide.item", "hideIcon", 196f - off, 0f, 24f, 24f).apply {
                    transparent()
                    borderless()
                    metadata += "ref" to obj.ref
                    setTooltip("Hide object")
                }
            } else {
                +IconButton("tree.view.show.item", "showIcon", 196f - off, 0f, 24f, 24f).apply {
                    transparent()
                    borderless()
                    metadata += "ref" to obj.ref
                    setTooltip("Show object")
                }
            }

            +IconButton("tree.view.delete.item", "deleteIcon", 222f - off, 0f, 24f, 24f).apply {
                transparent()
                borderless()
                metadata += "ref" to obj.ref
                setTooltip("Delete object")
            }
        }
    }
}

class ToggleName : RComponent<ToggleName.Props, ToggleName.State>() {

    override fun getInitialState() = State(true)

    override fun RBuilder.render() {

        if (state.hidden) {
            comp(TextButton("", props.group.name, 24f, 0f, 172f - props.offset, 24f)) {
                style {
                    transparent()
                    borderless()
                    fontSize = 20f
                    horizontalAlign = HorizontalAlign.LEFT
                    textState.padding.x = 2f
                    metadata += "ref" to props.group.ref
                }
                onDoubleClick { setState { copy(hidden = false) } }
                onClick { props.dispatcher.onEvent("tree.view.select.group", it.targetComponent) }
            }
        } else {
            comp(StringInput("model.group.change.name", props.group.name, 24f, 0f, 172f - props.offset, 24f)) {
                style {
                    transparent()
                    borderless()
                    textState.horizontalAlign = HorizontalAlign.LEFT
                    textState.fontSize = 20f
                    textState.padding.x = 2f
                    metadata += "ref" to props.group.ref
                }

                onFocus {
                    if (!it.isFocused) setState { copy(hidden = true) }
                }
            }
        }
    }

    data class State(val hidden: Boolean) : RState
    data class Props(val group: IGroup, val offset: Float, val dispatcher: Dispatcher) : RProps
}


class ModelTreeAnimation(val modelAccessor: IModelAccessor, val objMap: List<Slot>, val component: Component, val input: IInput, val reset: () -> Unit, val dispatcher: Dispatcher) : Animation() {
    var pressTime = 0L
    var unPressTime = 0L
    var selected: Int? = null
    var initialMousePos: IVector2 = Vector2.ORIGIN
    var containerStartHeight: Float = 0f

    override fun animate(delta: Double): Boolean {
        val now = Timer.miliTime.toLong()
        val mousePos = input.mouse.getMousePos()

        if (input.mouse.isButtonPressed(Mouse.BUTTON_LEFT)) {
            pressTime = now
        } else {
            if (selected != null) {
                applyChanges()
                selected = null
                containerStartHeight = 0f
            }
            initialMousePos = mousePos
            unPressTime = now
        }

        if (selected == null && pressTime - unPressTime > 500) {
            if (initialMousePos == mousePos) {
                val mPos = mousePos.toJoml2f()

                component.childComponents.forEachIndexed { index, comp ->
                    if (comp.intersects(mPos)) {
                        selected = index
                        containerStartHeight = comp.absolutePosition.y - comp.position.y
                        initialMousePos = mPos.toIVector()
                    }
                }
            }

            if (selected == null) {
                // try again later
                initialMousePos = mousePos
                unPressTime = pressTime
            }
        }

        selected?.let { sel ->
            val coords = calculateMouseCoords()
            val selected: (Int, Slot) -> Boolean

            selected = if (isMultiSelect(sel))
                { _, obj -> modelAccessor.modelSelection.eval { obj.obj != null && it.isSelected(obj.obj) } }
            else
                { i, _ -> i == sel }

            component.childComponents.forEachIndexed { index, component ->
                component.borderless()

                objMap.getOrNull(index)?.let { slot ->
                    if (selected(index, slot)) {
                        component.style.border = SimpleLineBorder(ColorConstants.blue(), 2f)
                    }
                }
            }
            component.childComponents.getOrNull(coords.y)?.let { comp ->
                comp.style.border = SimpleLineBorder(ColorConstants.red(), 2f)
            }
        }
        return false
    }

    private fun isMultiSelect(sel: Int): Boolean {
        return objMap[sel].obj != null && modelAccessor.modelSelection.eval { it.isSelected(objMap[sel].obj!!) }
    }

    private fun calculateMouseCoords(): Vector2i {
        val mPos = input.mouse.getMousePos()
        val itemUnderMouseHeight = mPos.yf - containerStartHeight
        val index = (itemUnderMouseHeight / 26f).toInt()

        val slot = objMap.getOrNull(index)
        val level = if (slot != null) if (slot.group != null) slot.level + 1 else slot.level else 0

        return Vector2i(level, index)
    }

    fun applyChanges() {
        reset()

        selected?.let { sel ->
            val coords = calculateMouseCoords()
            val slot = objMap[sel]

            val replaced = objMap.getOrNull(coords.y) ?: return
            val replacedGroup = replaced.group
            val replacedObj = replaced.obj

            val parent = when {
                replacedGroup != null -> replacedGroup
                replacedObj != null -> modelAccessor.model.getObjectGroup(replacedObj)
                else -> return
            }

            dispatcher.onEvent("model.tree.node.moved", Panel().apply {
                metadata += "parent" to parent
                metadata += "child" to slot
                metadata += "multi" to isMultiSelect(sel)
            })
        }
    }
}