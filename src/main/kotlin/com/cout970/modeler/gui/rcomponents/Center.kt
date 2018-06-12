package com.cout970.modeler.gui.rcomponents

import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.canvas.CanvasContainer
import com.cout970.modeler.gui.event.EventNotificationUpdate
import com.cout970.modeler.gui.event.NotificationHandler
import com.cout970.modeler.gui.leguicomp.FixedLabel
import com.cout970.modeler.gui.leguicomp.Panel
import com.cout970.modeler.gui.leguicomp.ProfilerDiagram
import com.cout970.modeler.gui.leguicomp.classes
import com.cout970.modeler.util.toColor
import com.cout970.reactive.core.EmptyProps
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RProps
import com.cout970.reactive.core.RStatelessComponent
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.child
import com.cout970.reactive.nodes.comp
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.style
import org.liquidengine.legui.component.TextArea
import org.liquidengine.legui.component.optional.align.HorizontalAlign

data class CenterPanelProps(val canvasContainer: CanvasContainer, val timer: Timer) : RProps

class CenterPanel : RStatelessComponent<CenterPanelProps>() {

    override fun RBuilder.render() = div("CenterPanel") {

        style {
            transparent()
            borderless()
        }

        postMount {
            val left = if (parent.child("LeftPanel")?.isEnabled == true) 288f else 0f
            val right = if (parent.child("RightPanel")?.isEnabled == true) 288f else 0f
            val bottom = if (parent.child("BottomPanel")?.isEnabled == true) 200f else 0f
            posX = left
            posY = 48f
            width = parent.size.x - left - right
            height = parent.size.y - 48f - bottom
        }

        // Canvas used to draw 3D stuff
        comp(Panel()) {
            style {
                posX = 0f
                posY = 0f
                borderless()
                transparent()

                if (props.canvasContainer.canvas.isEmpty()) {
                    hide()
                }
            }

            postMount {
                fill()
                props.canvasContainer.also {
                    it.panel = this as Panel
                    it.refreshCanvas()
                    it.layout.updateCanvas()
                }
            }
        }

        div("HelpKeyBinds") {

            style {
                transparent()
                borderless()
            }

            postMount {
                sizeX = 230f
                sizeY = 150f
                center()

                if (props.canvasContainer.panel.isEnabled) {
                    hide()
                }
            }

            // first level
            +FixedLabel("Open new view:", 0f, 0f, 100f, 24f)
                    .apply { textState.horizontalAlign = HorizontalAlign.LEFT; textState.fontSize = 20f }

            +FixedLabel("Close view:", 0f, 25f, 100f, 24f)
                    .apply { textState.horizontalAlign = HorizontalAlign.LEFT; textState.fontSize = 20f }

            +FixedLabel("Resize view:", 0f, 50f, 100f, 24f)
                    .apply { textState.horizontalAlign = HorizontalAlign.LEFT; textState.fontSize = 20f }

            +FixedLabel("Change mode:", 0f, 75f, 100f, 24f)
                    .apply { textState.horizontalAlign = HorizontalAlign.LEFT; textState.fontSize = 20f }

            +FixedLabel("Hide left:", 0f, 100f, 100f, 24f)
                    .apply { textState.horizontalAlign = HorizontalAlign.LEFT; textState.fontSize = 20f }

            +FixedLabel("Hide right:", 0f, 125f, 100f, 24f)
                    .apply { textState.horizontalAlign = HorizontalAlign.LEFT; textState.fontSize = 20f }

            // second level

            +FixedLabel("Alt + N", 150f, 0f, 100f, 24f)
                    .apply { textState.horizontalAlign = HorizontalAlign.LEFT; textState.fontSize = 20f }

            +FixedLabel("Alt + D", 150f, 25f, 100f, 24f)
                    .apply { textState.horizontalAlign = HorizontalAlign.LEFT; textState.fontSize = 20f }

            +FixedLabel("Alt + J/K", 150f, 50f, 100f, 24f)
                    .apply { textState.horizontalAlign = HorizontalAlign.LEFT; textState.fontSize = 20f }

            +FixedLabel("Alt + M", 150f, 75f, 100f, 24f)
                    .apply { textState.horizontalAlign = HorizontalAlign.LEFT; textState.fontSize = 20f }

            +FixedLabel("Alt + L", 150f, 100f, 100f, 24f)
                    .apply { textState.horizontalAlign = HorizontalAlign.LEFT; textState.fontSize = 20f }

            +FixedLabel("Alt + R", 150f, 125f, 100f, 24f)
                    .apply { textState.horizontalAlign = HorizontalAlign.LEFT; textState.fontSize = 20f }

        }

        +ProfilerDiagram(props.timer)

        child(EventPanel::class)
    }
}

class EventPanel : RStatelessComponent<EmptyProps>() {

    override fun RBuilder.render() = div("EventPanel") {

        val notifications = NotificationHandler.getNotifications()

        style {
            transparent()
        }

        postMount {
            width = 330f
            height = (65f + 4f) * notifications.size
            posX = parent.sizeX - width - 10f
            posY = parent.sizeY - height - 10f
            if (notifications.isEmpty()) hide()
        }

        on<EventNotificationUpdate> {
            rerender()
        }

        notifications.asReversed().forEachIndexed { index, notification ->
            div {
                style {
                    width = 330f
                    height = 65f
                    posY = index * (height + 4f)
                    classes("notification")
                }

                +FixedLabel(notification.title, y = 0f, width = 330f, height = 24f).apply {
                    textState.fontSize = 18f
                }
                +TextArea(notification.text, 0f, 24f, 330f, 40f).apply {
                    isEditable = false
                    textState.textColor = Config.colorPalette.textColor.toColor()
                    transparent()
                }
            }
        }
    }
}