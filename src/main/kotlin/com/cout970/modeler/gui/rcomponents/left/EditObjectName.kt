package com.cout970.modeler.gui.rcomponents.left

import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.event.EventModelUpdate
import com.cout970.modeler.gui.event.EventSelectionUpdate
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.util.Nullable
import com.cout970.modeler.util.getOr
import com.cout970.modeler.util.toColor
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RComponent
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.comp
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.style
import org.joml.Vector2f
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.icon.CharIcon
import org.liquidengine.legui.style.color.ColorConstants
import org.liquidengine.legui.style.font.FontRegistry

class EditObjectName : RComponent<ModelAccessorProps, VisibleWidget>() {

    override fun getInitialState() = VisibleWidget(true)

    override fun RBuilder.render() = div("EditObjectName") {
        style {
            transparent()
            border(2f) { greyColor }
            rectCorners()
            height = if (state.on) 64f else 24f
        }

        postMount {
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
            onRelease {
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