package com.cout970.modeler.gui.rcomponents.right

import com.cout970.glutilities.device.Mouse
import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.*
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.controller.Dispatch
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.model.ref
import com.cout970.modeler.core.model.selection.ObjectRefNone
import com.cout970.modeler.core.project.IProgramState
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.gui.sendCmd
import com.cout970.modeler.input.event.IInput
import com.cout970.modeler.util.*
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
import org.liquidengine.legui.system.context.Context
import kotlin.math.min

data class Slot(val obj: IObjectRef?, val group: IGroupRef?, val level: Int)

data class ModelTreeProps(val programState: IProgramState, val input: IInput) : RProps
data class ModelTreeState(val selectedObj: IObjectRef) : RState

private inline val LEVEL_WIDTH: Float get() = Config.modelTreeScale
private inline val LEVEL_HEIGHT: Float get() = Config.modelTreeScale
private inline val ICON_WIDTH: Float get() = Config.modelTreeScale

class ModelTree : RComponent<ModelTreeProps, ModelTreeState>() {

    var animation: Animation? = null

    override fun getInitialState() = ModelTreeState(ObjectRefNone)

    fun generateObjectMap(): List<Slot> {

        val model = props.programState.model
        val tree = model.tree
        val map = mutableListOf<Slot>()

        tree.groups[RootGroupRef].forEach {
            addGroupAndChildren(model, tree, it, map, 0)
        }

        tree.objects[RootGroupRef].forEach { ref ->
            map += Slot(ref, null, 0)
        }

        return map
    }

    fun addGroupAndChildren(model: IModel, tree: ImmutableGroupTree, group: IGroupRef, map: MutableList<Slot>, level: Int) {

        map += Slot(null, group, level)

        if (!model.getGroup(group).visible) return

        tree.groups[group].forEach {
            addGroupAndChildren(model, tree, it, map, min(5, level + 1))
        }

        tree.objects[group].forEach { ref ->
            map += Slot(ref, null, min(5, level + 2))
        }
    }

    override fun RBuilder.render() = div("ModelTree") {

        style {
            classes("left_panel_model_tree")
            posY = 0f
        }

        postMount {
            marginX(5f)
            height = (parent.height) / 2f
            alignAsColumn(5f)
        }

        onCmd("updateModel") { rerender() }
        onCmd("updateSelection") { rerender() }

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
                sizeY = 24f
                marginX(0f)
            }
        }

        div("Buttons") {
            style {
                sizeY = 32f
                borderless()
                transparent()
            }
            postMount {
                marginX(5f)
            }

            +IconButton("cube.template.new", "addTemplateCubeIcon", 5f, 0f, 32f, 32f).also {
                it.setTooltip("Create Cube")
            }
            +IconButton("cube.mesh.new", "addMeshCubeIcon", 40f, 0f, 32f, 32f).also {
                it.setTooltip("Create Mesh")
            }
            +IconButton("group.add", "addMeshCubeIcon", 75f, 0f, 32f, 32f).also {
                it.setTooltip("Create Group")
            }
        }

        scrollablePanel("ModeTreeScrollPanel") {

            style {
                transparent()
                borderless()
            }

            postMount {
                posX = 5f
                sizeX = parent.sizeX - 5f
                sizeY = parent.sizeY - posY - 10f
            }

            horizontalScroll {
                style { hide() }
            }

            verticalScroll {
                style {
                    borderless()
                    style.minWidth = 16f
                    arrowColor = color { bright1 }
                    visibleAmount = 50f
                    style.top = 0f
                    style.bottom = 0f
                    classes("left_panel_model_tree_scroll")
                }
            }

            viewport {
                style {
                    style.right = 18f
                    style.bottom = 0f
                    classes("left_panel_model_tree_box")
                }
            }

            container {

                val model = props.programState.model
                val objectMap = generateObjectMap()
                val selected = props.programState.modelSelection
                    .map { sel -> { obj: IObjectRef -> sel.isSelected(obj) } }
                    .getOr({ _: IObjectRef -> false })

                style {
                    transparent()
                    borderless()
                    height = objectMap.size * (LEVEL_HEIGHT + 2f) + 10f
                    width = 251f
                }

                postMount {
                    animation?.stopAnimation()
                    val anim = ModelTreeAnimation(props.programState, objectMap,
                        this, props.input, this@ModelTree::rerender)

                    animation = anim.apply { startAnimation() }
                }

                val allowDrop = props.programState.modelSelection.isNonNull()

                objectMap.forEachIndexed { index, slot ->
                    val group = slot.group
                    val obj = slot.obj

                    if (group != null) {
                        val children = model.tree.groups[group].isNotEmpty() || model.tree.objects[group].isNotEmpty()
                        group(index, slot.level, model.getGroup(group), children, allowDrop)
                    } else if (obj != null) {
                        obj(index, slot.level, model.getObject(obj), selected(obj))
                    }
                }
            }
        }
    }

    fun RBuilder.group(index: Int, level: Int, group: IGroup, hasChilds: Boolean, allowDrop: Boolean) {
        val cmd = if (group.visible) "tree.view.hide.group" else "tree.view.show.group"
        val off = (level + 1) * LEVEL_WIDTH
        var textWidth = 246f - ICON_WIDTH * 2 - (if (allowDrop) ICON_WIDTH else 0f)

        val icon = if (hasChilds) {
            if (group.visible) "button_down" else "button_right"
        } else "button_right_dark"

        +IconButton(cmd, icon, 0f, 0f, LEVEL_WIDTH, LEVEL_HEIGHT).apply {
            posX = 5f + level * LEVEL_WIDTH
            posY = 5f + index * (sizeY + 2f)
            metadata += "ref" to group.ref
        }

        div(group.id.toString()) {
            style {
                sizeY = LEVEL_HEIGHT
                posX = 5f + off
                posY = 5f + index * (sizeY + 2f)
                classes("model_tree_item")
                if (props.programState.selectedGroup == group.ref) {
                    classes("model_tree_selected_group")
                }
            }

            postMount {
                sizeX = parent.sizeX - 5f - level * LEVEL_WIDTH - ICON_WIDTH - 1f
            }

            +IconButton(cmd, "group_icon", 0f, 0f, ICON_WIDTH, LEVEL_HEIGHT).apply {
                metadata += "ref" to group.ref
                classes("model_tree_item_icon")
            }

            child(ToggleName::class, ToggleName.Props(group.ref, group.name, off, textWidth, "tree.view.select.group", "model.group.change.name"))

            if (allowDrop) {
                textWidth += ICON_WIDTH
                +IconButton("tree.view.select.group", "moveToGroupIcon", textWidth - off, 0f, ICON_WIDTH, LEVEL_HEIGHT).apply {
                    metadata += "ref" to group.ref
                    metadata += "append" to true
                    setTooltip("Add objects to group")
                    classes("model_tree_item_icon")
                }
            }

            +IconButton("tree.view.delete.group", "deleteIcon", textWidth + ICON_WIDTH - off, 0f, ICON_WIDTH, LEVEL_HEIGHT).apply {
                metadata += "ref" to group.ref
                setTooltip("Delete group")
                classes("model_tree_item_icon")
            }
        }
    }

    fun RBuilder.obj(index: Int, level: Int, obj: IObject, selected: Boolean) {
        div(obj.id.toString()) {
            style {
                sizeY = LEVEL_HEIGHT
                posX = 5f + LEVEL_WIDTH * level
                posY = 5f + index * (sizeY + 2f)

                classes("model_tree_item")

                if (selected) {
                    classes("model_tree_item_selected")
                }
            }

            postMount {
                sizeX = parent.sizeX - 5f - level * LEVEL_WIDTH
            }

            val icon = if (obj is IObjectCube) "obj_type_cube" else "obj_type_mesh"
            val off = level * LEVEL_WIDTH
            val textWidth = 246f - ICON_WIDTH * 3

            +IconButton("tree.view.select.item", icon, 0f, 0f, ICON_WIDTH, LEVEL_HEIGHT).apply {
                hoveredStyle.background.color.set(0f)
                metadata += "ref" to obj.ref
                classes("model_tree_item_icon")
            }

            child(ToggleName::class, ToggleName.Props(obj.ref, obj.name, off, textWidth, "tree.view.select.item", "model.obj.change.name"))

            val showIcon = if (obj.visible) "hideIcon" else "showIcon"
            val showTooltip = if (obj.visible) "Hide object" else "Show object"
            val showAction = if (obj.visible) "tree.view.hide.item" else "tree.view.show.item"

            +IconButton(showAction, showIcon, textWidth + ICON_WIDTH - off, 0f, ICON_WIDTH, LEVEL_HEIGHT).apply {
                metadata += "ref" to obj.ref
                setTooltip(showTooltip)
                classes("model_tree_item_icon")
            }

            +IconButton("tree.view.delete.item", "deleteIcon", textWidth + ICON_WIDTH * 2 - off, 0f, ICON_WIDTH, LEVEL_HEIGHT).apply {
                metadata += "ref" to obj.ref
                setTooltip("Delete object")
                classes("model_tree_item_icon")
            }
        }
    }
}

class ToggleName : RComponent<ToggleName.Props, ToggleName.State>() {

    var timer = 0.0
    var context: Context? = null

    override fun getInitialState() = State(true)

    override fun RBuilder.render() = div {
        style {
            posX = ICON_WIDTH
            posY = 0f
            sizeX = props.width - props.offset
            sizeY = LEVEL_HEIGHT
            transparent()
            borderless()
        }
        if (state.hidden) {
            comp(TextButton("", props.text, 0f, 0f, props.width - props.offset, LEVEL_HEIGHT)) {
                style {
                    paddingLeft(2f)
                    metadata += "ref" to props.ref
                    classes("model_tree_item_text")
                }

                onClick {
                    if (Timer.miliTime - timer < 500) {
                        sendCmd("setToggleNameActive")
                        context = it.context
                        setState {
                            copy(hidden = false)
                        }
                    } else {
                        Dispatch.run(props.clickEvent, it.targetComponent)
                    }
                    timer = Timer.miliTime
                }
            }
        } else {
            comp(StringInput(props.textEvent, props.text, 0f, 0f, props.width - props.offset, LEVEL_HEIGHT)) {
                style {
                    metadata += "ref" to props.ref
                    classes("model_tree_item_text", "model_tree_item_text_field")
                    val oldOnLoseFocus = onLoseFocus
                    onLoseFocus = {
                        oldOnLoseFocus?.invoke()
                        setState { copy(hidden = true) }
                    }

                    val oldOnTextChange = onTextChange
                    onTextChange = {
                        oldOnTextChange?.invoke(it)
                        context?.focus(this)
                    }

                    context?.focus(this)
                }
            }
        }
        onCmd("setToggleNameActive") {
            setState { copy(hidden = true) }
        }
    }

    override fun shouldComponentUpdate(nextProps: Props, nextState: State): Boolean {
        return true
    }

    data class State(val hidden: Boolean) : RState
    data class Props(val ref: Any, val text: String, val offset: Float, val width: Float, val clickEvent: String, val textEvent: String) : RProps
}

private const val TIME_THRESHOLD_FOR_MOVE_OBJECT = 200

class ModelTreeAnimation(val programState: IProgramState, val objMap: List<Slot>, val component: Component, val input: IInput, val reset: () -> Unit) : Animation() {
    var pressTime = 0L
    var unPressTime = 0L
    var selected: Int? = null
    var initialMousePos: IVector2 = Vector2.ORIGIN
    var containerStartPosY: Float = 0f
    var initialScroll: Float = 0f

    override fun animate(delta: Double): Boolean {
        val now = Timer.miliTime.toLong()
        val mousePos = input.mouse.getMousePos()

        if (input.mouse.isButtonPressed(Mouse.BUTTON_LEFT)) {
            pressTime = now
        } else {
            if (selected != null) {
                applyChanges()
                selected = null
                containerStartPosY = 0f
                initialScroll = 0f
            }
            initialMousePos = mousePos
            unPressTime = now
        }

        if (selected == null && pressTime - unPressTime > TIME_THRESHOLD_FOR_MOVE_OBJECT) {
            if (initialMousePos == mousePos) {
                val mPos = mousePos.toJoml2f()

                component.childComponents.filterIsInstance<Panel>().forEachIndexed { index, comp ->
                    if (comp.intersects(mPos)) {
                        selected = index
                        containerStartPosY = comp.absolutePosition.y - comp.position.y
                        initialScroll = component.posY
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
                { _, obj -> programState.modelSelection.eval { obj.obj != null && it.isSelected(obj.obj) } }
            else
                { i, _ -> i == sel }

            component.childComponents.filterIsInstance<Panel>().forEachIndexed { index, component ->
                component.borderless()

                objMap.getOrNull(index)?.let { slot ->
                    if (selected(index, slot)) {
                        component.style.border = SimpleLineBorder(ColorConstants.blue(), 2f)
                    }
                }
            }
            component.childComponents.filterIsInstance<Panel>().getOrNull(coords.y)?.let { comp ->
                comp.style.border = SimpleLineBorder(ColorConstants.red(), 2f)
            }
        }
        return false
    }

    private fun isMultiSelect(sel: Int): Boolean {
        return objMap[sel].obj != null && programState.modelSelection.eval { it.isSelected(objMap[sel].obj!!) }
    }

    private fun calculateMouseCoords(): Vector2i {
        val mPos = input.mouse.getMousePos()
        val scrollDiff = component.posY - initialScroll
        val itemUnderMouseHeight = mPos.yf - containerStartPosY - scrollDiff
        val index = (itemUnderMouseHeight / (LEVEL_HEIGHT + 2f)).toInt()

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
                replacedObj != null -> programState.model.tree.objects.getReverse(replacedObj)
                else -> return
            }

            Dispatch.run("model.tree.node.moved") {
                this["parent"] = parent
                this["child"] = slot
                this["multi"] = isMultiSelect(sel)
            }
        }
    }
}