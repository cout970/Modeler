package com.cout970.modeler.gui.components

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
import org.joml.Vector2f
import org.liquidengine.legui.color.ColorConstants
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.font.FontRegistry
import org.liquidengine.legui.icon.CharIcon

/**
 * Created by cout970 on 2017/10/29.
 */
class EditObjectName : RComponent<EditObjectName.Props, Unit>() {

    init {
        state = Unit
    }

    override fun build(ctx: RBuilder): Component = panel {

        marginX(ctx, 5f)
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

        +StringInput(text, 10f, 24f, width - 20f, 32f).apply {
            textState.horizontalAlign = HorizontalAlign.CENTER
            backgroundColor = Config.colorPalette.greyColor.toColor()
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

    companion object : RComponentSpec<EditObjectName, EditObjectName.Props, Unit>
}