package com.cout970.modeler.gui.components.left

import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.controller.Dispatcher
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.project.IModelAccessor
import com.cout970.modeler.gui.event.EventModelUpdate
import com.cout970.modeler.gui.event.EventSelectionUpdate
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.gui.reactive.RBuilder
import com.cout970.modeler.gui.reactive.RComponent
import com.cout970.modeler.gui.reactive.RComponentSpec
import com.cout970.modeler.util.disable
import com.cout970.modeler.util.setTransparent
import com.cout970.modeler.util.toColor
import com.cout970.reactive.dsl.height
import com.cout970.reactive.dsl.posY
import com.cout970.reactive.dsl.width
import org.joml.Vector2f
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.icon.CharIcon
import org.liquidengine.legui.style.color.ColorConstants
import org.liquidengine.legui.style.font.FontRegistry

/**
 * Created by cout970 on 2017/10/29.
 */
class EditObjectName : RComponent<EditObjectName.Props, Unit>() {

    init {
        state = Unit
    }

    override fun build(ctx: RBuilder): Component = panel {

        marginX(5f)
        posY = props.posY
        height = if (props.visible) 64f else 24f
        setTransparent()
        border(3f) { greyColor }

        listenerMap.addListener(EventModelUpdate::class.java) {
            replaceState(state)
        }
        listenerMap.addListener(EventSelectionUpdate::class.java) {
            replaceState(state)
        }

        val model = props.access.model
        val selection = props.access.modelSelection
        val obj = selection
                .filter { it.size == 1 }
                .flatMap { it.refs.firstOrNull() }
                .filterIsInstance<IObjectRef>()
                .map { model.getObject(it) }

        val text = obj.map { it.name }.getOr("")

        +FixedLabel("Object Name", 50f, 0f, width - 100f, 24f).apply {
            textState.textColor = Config.colorPalette.textColor.toColor()
            textState.horizontalAlign = HorizontalAlign.CENTER
            textState.fontSize = 20f
        }

        // close button
        +IconButton(posX = 250f, posY = 4f).apply {
            if (props.visible) {
                setImage(CharIcon(Vector2f(16f, 16f), FontRegistry.DEFAULT, 'X', ColorConstants.lightGray()))
            } else {
                setImage(CharIcon(Vector2f(16f, 16f), FontRegistry.DEFAULT, 'O', ColorConstants.lightGray()))
            }
            background { darkColor }
            onClick { props.toggle() }
        }

        +StringInput("", text, 10f, 24f, width - 20f, 32f).apply {
            textState.horizontalAlign = HorizontalAlign.CENTER
            background { greyColor }
            textState.fontSize = 24f
            obj.ifNull {
                isEditable = false
                this.disable()
            }
            obj.ifNotNull {
                onLoseFocus = {
                    props.dispatcher.onEvent("model.obj.change.name", this)
                }
                onEnterPress = onLoseFocus
                onTextChange = {
                    props.dispatcher.onEvent("model.obj.change.name", this)
                }
            }
        }
    }

    class Props(val access: IModelAccessor, val dispatcher: Dispatcher,
                val posY: Float, val visible: Boolean, val toggle: () -> Unit)

    companion object : RComponentSpec<EditObjectName, Props, Unit>
}