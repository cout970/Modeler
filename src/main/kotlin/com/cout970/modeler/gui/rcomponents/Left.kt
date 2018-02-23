package com.cout970.modeler.gui.rcomponents

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.`object`.IObjectCube
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.model.*
import com.cout970.modeler.core.model.selection.ObjectRefNone
import com.cout970.modeler.core.project.IModelAccessor
import com.cout970.modeler.gui.canvas.GridLines
import com.cout970.modeler.gui.event.EventModelUpdate
import com.cout970.modeler.gui.event.EventSelectionUpdate
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.util.Nullable
import com.cout970.modeler.util.asNullable
import com.cout970.modeler.util.toColor
import com.cout970.reactive.core.*
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.*
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.Vector2
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.vec2Of
import org.joml.Vector2f
import org.liquidengine.legui.component.CheckBox
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.icon.CharIcon
import org.liquidengine.legui.style.color.ColorConstants
import org.liquidengine.legui.style.font.FontRegistry

data class LeftPanelProps(val modelAccessor: IModelAccessor, val grids: GridLines) : RProps

class LeftPanel : RStatelessComponent<LeftPanelProps>() {

    override fun RBuilder.render() = div("LeftPanel") {
        style {
            background { darkestColor }
            borderless()
            posX = 0f
            posY = 48f
        }

        postMount {
            width = 288f
            height = parent.size.y - 48f
        }


        // TODO add support for scroll panels
        comp(Panel()) {

            println("[LeftPanel] render")
            style {
                //                verticalScrollBar.visibleAmount = 20f
//                viewport.listenerMap.clear(ScrollEvent::class.java)
                borderless()
                transparent()
            }

            postMount {
                println("[LeftPanel] postMount")
                posX = 0f
                posY = 0f
                sizeX = parent.sizeX - 8f
                fillY()
                alignAsColumn(6f)
//                this as VerticalPanel
//                container.apply {
//                    width = 280f
//                    height = 64f + 486f + 345f
//                }
            }

            child(EditObjectName::class, ModelAccessorProps(props.modelAccessor))
            child(EditCubePanel::class, ModelAccessorProps(props.modelAccessor))
            child(EditGrids::class, EditGridsProps(props.grids))
        }
    }
}


data class VisibleWidget(val on: Boolean) : RState
data class ModelAccessorProps(val access: IModelAccessor) : RProps

class EditObjectName : RComponent<ModelAccessorProps, VisibleWidget>() {

    override fun getInitialState() = VisibleWidget(true)

    override fun RBuilder.render() = div("EditObjectName") {
        println("[EditObjectName] render")
        style {
            transparent()
            border(2f) { greyColor }
            rectCorners()
            height = if (state.on) 64f else 24f
        }

        postMount {
            println("[EditObjectName] postMount")
            marginX(5f)
        }

        on<EventModelUpdate> {
            rerender()
        }
        on<EventSelectionUpdate> {
            rerender()
        }

        val obj = getObject()
        val text = obj.map { it.name }.getOr("")

        comp(FixedLabel()) {
            style {
                textState.apply {
                    this.text = "Object Name"
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

        // close button
        comp(IconButton()) {
            style {
                val charCode = if (state.on) 'X' else 'O'
                setImage(CharIcon(Vector2f(16f, 16f), FontRegistry.DEFAULT, charCode, ColorConstants.lightGray()))
                background { darkColor }
                posX = 250f
                posY = 4f
            }
            onClick {
                setState { copy(on = !on) }
            }
        }

        comp(StringInput("model.obj.change.name")) {
            style {
                background { greyColor }
                textState.horizontalAlign = HorizontalAlign.CENTER
                textState.text = text
                textState.fontSize = 24f
            }

            postMount {
                posX = 10f
                posY = 24f
                sizeX = parent.sizeX - 20f
                sizeY = 32f

                this as StringInput

                obj.ifNull {
                    isEditable = false
                    isEnabled = false
                }

                metadata["obj"] = obj
            }
        }
    }

    private fun getObject(): Nullable<IObject> {
        val model = props.access.model
        val selection = props.access.modelSelection
        return selection
                .filter { it.size == 1 }
                .flatMap { it.refs.firstOrNull() }
                .filterIsInstance<IObjectRef>()
                .map { model.getObject(it) }
    }
}

class EditCubePanel : RComponent<ModelAccessorProps, VisibleWidget>() {

    override fun getInitialState() = VisibleWidget(true)

    override fun RBuilder.render() = div("EditCubePanel") {
        style {
            transparent()
            border(2f) { greyColor }
            rectCorners()
            height = if (state.on) 486f else 24f
        }

        postMount {
            marginX(5f)
            alignAsColumn(5f)
        }

        on<EventModelUpdate> {
            rerender()
        }
        on<EventSelectionUpdate> {
            rerender()
        }

        val (ref, cube) = getObject().split { it }
        val cubeRef: IObjectRef = ref.getOr(ObjectRefNone)

        val pos = { cube.map { it.pos }.getOr(Vector3.ORIGIN) }
        val rotation = { cube.map { it.rotation }.getOr(Vector3.ORIGIN) }
        val size = { cube.map { it.size }.getOr(Vector3.ORIGIN) }
        val tex = { cube.map { it.textureOffset }.getOr(Vector2.ORIGIN) }
        val scale = { cube.map { it.textureSize }.getOr(Vector2.ORIGIN) }

        div("Title") {
            style {
                transparent()
                borderless()
                sizeY = 24f
                posY = 1f
            }

            postMount {
                fillX()
            }

            comp(FixedLabel()) {
                style {
                    textState.text = "Edit Cube"
                    fontSize = 22f
                    posX = 50f
                    posY = 0f
                    sizeY = 22f
                }

                postMount {
                    sizeX = parent.sizeX - 100
                }
            }

            // close button
            +IconButton(posX = 250f, posY = 3f).apply {
                val charCode = if (state.on) 'X' else 'O'
                setImage(CharIcon(Vector2f(16f, 16f), FontRegistry.DEFAULT, charCode, ColorConstants.lightGray()))
                background { darkColor }

                onClick { setState { copy(on = !on) } }
            }
        }

        div("Size") {
            style {
                transparent()
                borderless()
                height = 110f
            }

            postMount {
                fillX()
            }

            +FixedLabel("Size", 0f, 0f, 278f, 18f).apply { fontSize = 22f }
            +FixedLabel("x", 10f, 90f, 75f, 20f).apply { fontSize = 18f }
            +FixedLabel("y", 98f, 90f, 75f, 20f).apply { fontSize = 18f }
            +FixedLabel("z", 185f, 90f, 75f, 20f).apply { fontSize = 18f }

            valueInput({ size().xf }, "cube.size.x", cubeRef, vec2Of(10f, 20f))
            valueInput({ size().yf }, "cube.size.y", cubeRef, vec2Of(98f, 20f))
            valueInput({ size().zf }, "cube.size.z", cubeRef, vec2Of(185f, 20f))
        }

        div("Position") {
            style {
                transparent()
                borderless()
                height = 110f
            }

            postMount {
                fillX()
            }

            +FixedLabel("Position", 0f, 0f, 278f, 18f).apply { textState.fontSize = 22f }
            +FixedLabel("x", 10f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("y", 98f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("z", 185f, 90f, 75f, 20f).apply { textState.fontSize = 18f }

            valueInput({ pos().xf }, "cube.pos.x", cubeRef, vec2Of(10f, 20f))
            valueInput({ pos().yf }, "cube.pos.y", cubeRef, vec2Of(98f, 20f))
            valueInput({ pos().zf }, "cube.pos.z", cubeRef, vec2Of(185f, 20f))
        }

        div("Rotation") {
            style {
                transparent()
                borderless()
                height = 110f
            }

            postMount {
                fillX()
            }

            +FixedLabel("Rotation", 0f, 0f, 278f, 18f).apply { textState.fontSize = 22f }
            +FixedLabel("x", 10f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("y", 98f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("z", 185f, 90f, 75f, 20f).apply { textState.fontSize = 18f }

            valueInput({ rotation().xf }, "cube.rot.x", cubeRef, vec2Of(10f, 20f))
            valueInput({ rotation().yf }, "cube.rot.y", cubeRef, vec2Of(98f, 20f))
            valueInput({ rotation().zf }, "cube.rot.z", cubeRef, vec2Of(185f, 20f))
        }

        div("Texture") {
            style {
                transparent()
                borderless()
                height = 110f
            }

            postMount {
                fillX()
            }

            +FixedLabel("Texture", 0f, 0f, 278f, 18f).apply { textState.fontSize = 22f }
            +FixedLabel("x", 10f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("y", 98f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("scale", 185f, 90f, 75f, 20f).apply { textState.fontSize = 18f }

            valueInput({ tex().xf }, "cube.tex.x", cubeRef, vec2Of(10f, 20f))
            valueInput({ tex().yf }, "cube.tex.y", cubeRef, vec2Of(98f, 20f))
            valueInput({ scale().xf }, "cube.tex.scale", cubeRef, vec2Of(185f, 20f))
        }
    }

    fun isSelectingOneCube(model: IModel, new: ISelection): Boolean {
        if (new.selectionType != SelectionType.OBJECT) return false
        if (new.selectionTarget != SelectionTarget.MODEL) return false
        if (new.size != 1) return false
        val selectedObj = model.getSelectedObjects(new).firstOrNull() ?: return false
        return selectedObj is ObjectCube
    }

    fun getObject(): Nullable<Pair<IObjectRef, IObjectCube>> {
        return props.access.modelSelection
                .flatMap {
                    if (isSelectingOneCube(props.access.model, it)) it.objects.first() else null
                }
                .flatMapNullable {
                    val cube = props.access.model.getObject(it).asNullable().flatMap { it as? IObjectCube }
                    it.asNullable().zip(cube)
                }
    }

    fun DivBuilder.valueInput(getter: () -> Float, cmd: String, cubeRef: IObjectRef, pos: IVector2) {
        child(FloatInput::class, FloatInputProps(
                getter = getter,
                command = "update.template.cube",
                metadata = mapOf("cube_ref" to cubeRef, "command" to cmd),
                enabled = cubeRef != ObjectRefNone,
                pos = pos)
        )
    }
}

data class EditGridsProps(val gridLines: GridLines) : RProps

class EditGrids : RComponent<EditGridsProps, VisibleWidget>() {

    override fun getInitialState() = VisibleWidget(true)

    override fun RBuilder.render() = div("EditGrids") {
        style {
            transparent()
            border(2f) { greyColor }
            height = if (state.on) 345f else 24f
        }

        postMount {
            marginX(5f)
        }

        div("Title") {
            style {
                transparent()
                borderless()
                posY = 1f
                height = 24f
            }
            postMount {
                fillX()
            }

            comp(FixedLabel()) {
                style {
                    textState.text = "Config Grids"
                    fontSize = 22f
                    posX = 50f
                    sizeY = 22f
                }

                postMount {
                    sizeX = parent.sizeX - 100f
                }
            }

            // close button
            +IconButton(posX = 250f, posY = 3f).apply {
                val charCode = if (state.on) 'X' else 'O'
                setImage(CharIcon(Vector2f(16f, 16f), FontRegistry.DEFAULT, charCode, ColorConstants.lightGray()))
                background { darkColor }

                onClick { setState { copy(on = !on) } }
            }
        }

        div("Offset") {
            style {
                transparent()
                borderless()
                posY = 30f
                height = 110f
            }

            postMount {
                fillX()
            }

            val gridOffsetX = props.gridLines.gridOffset::xf.getter
            val gridOffsetY = props.gridLines.gridOffset::yf.getter
            val gridOffsetZ = props.gridLines.gridOffset::zf.getter

            +FixedLabel("Grid offset", 0f, 0f, 278f, 18f).apply { textState.fontSize = 22f }
            +FixedLabel("x", 10f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("y", 98f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("z", 185f, 90f, 75f, 20f).apply { textState.fontSize = 18f }

            valueInput(gridOffsetX, "offset", "x", vec2Of(10f, 20f))
            valueInput(gridOffsetY, "offset", "y", vec2Of(98f, 20f))
            valueInput(gridOffsetZ, "offset", "z", vec2Of(185f, 20f))
        }

        div("Size") {
            style {
                transparent()
                borderless()
                posY = 145f
                height = 110f
            }

            postMount {
                fillX()
            }

            val gridSizeX = props.gridLines.gridSize::xf.getter
            val gridSizeY = props.gridLines.gridSize::yf.getter
            val gridSizeZ = props.gridLines.gridSize::zf.getter

            +FixedLabel("Grid size", 0f, 0f, 278f, 18f).apply { textState.fontSize = 22f }
            +FixedLabel("x", 10f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("y", 98f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("z", 185f, 90f, 75f, 20f).apply { textState.fontSize = 18f }

            valueInput(gridSizeX, "size", "x", vec2Of(10f, 20f))
            valueInput(gridSizeY, "size", "y", vec2Of(98f, 20f))
            valueInput(gridSizeZ, "size", "z", vec2Of(185f, 20f))
        }

        div("Checkboxs") {
            style {
                transparent()
                borderless()
                posY = 110f + 110f + 10f + 25f
                height = 24f * 3 + 15f
            }

            postMount {
                marginX(10f)
            }

            +CheckBox("Enable Plane X", 0f, 0f, 278f - 10f, 24f).apply {
                defaultTextColor()
                style.cornerRadius.set(0f)
                fontSize = 20f
                textState.padding.x = 24f
                isChecked = props.gridLines.enableXPlane
                background { darkColor }

                configIcon(iconChecked as CharIcon)
                configIcon(iconUnchecked as CharIcon)

                onClick { props.gridLines.enableXPlane = isChecked; rerender() }
            }

            +CheckBox("Enable Plane Y", 0f, 24f + 5f, 278f - 10f, 24f).apply {
                defaultTextColor()
                fontSize = 20f
                textState.padding.x = 24f
                isChecked = props.gridLines.enableYPlane
                background { darkColor }

                configIcon(iconChecked as CharIcon)
                configIcon(iconUnchecked as CharIcon)

                onClick { props.gridLines.enableYPlane = isChecked; rerender() }
            }

            +CheckBox("Enable Plane Z", 0f, 48f + 10f, 278f - 10f, 24f).apply {
                defaultTextColor()
                fontSize = 20f
                textState.padding.x = 24f
                isChecked = props.gridLines.enableZPlane
                background { darkColor }

                configIcon(iconChecked as CharIcon)
                configIcon(iconUnchecked as CharIcon)

                onClick { props.gridLines.enableZPlane = isChecked; rerender() }
            }
        }
    }

    private fun configIcon(icon: CharIcon) {
        icon.color = Config.colorPalette.whiteColor.toColor()
        icon.position = Vector2f(4f, 4f)
        icon.horizontalAlign = HorizontalAlign.CENTER
    }

    fun DivBuilder.valueInput(getter: () -> Float, target: String, axis: String, pos: IVector2) {
        child(FloatInput::class, FloatInputProps(
                getter = getter,
                command = "grid.$target.change",
                metadata = mapOf("axis" to axis, "listener" to { rerender() }),
                enabled = true,
                pos = pos
        ))
    }
}