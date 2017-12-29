package com.cout970.modeler.gui.components

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.event.EventNotificationUpdate
import com.cout970.modeler.gui.event.NotificationHandler
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.gui.reactive.RBuilder
import com.cout970.modeler.gui.reactive.RComponent
import com.cout970.modeler.gui.reactive.RComponentSpec
import com.cout970.modeler.util.hide
import com.cout970.modeler.util.setTransparent
import com.cout970.modeler.util.toColor
import org.liquidengine.legui.border.SimpleLineBorder
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.TextArea

class EventPanel : RComponent<Unit, Unit>() {

    init {
        state = Unit
    }

    override fun build(ctx: RBuilder): Component = panel {

        val notifications = NotificationHandler.getNotifications()
        if (notifications.isEmpty()) hide()

        width = 330f
        height = (65f + 4f) * notifications.size
        posX = ctx.parentSize.xf - width - 10f
        posY = ctx.parentSize.yf - height - 10f

        setTransparent()

        notifications.asReversed().forEachIndexed { index, notification ->
            +panel {
                background { darkColor }
                border = SimpleLineBorder(Config.colorPalette.darkestColor.toColor(), 2f)
                width = 330f
                height = 65f
                posY = index * (height + 4f)
                +FixedLabel(notification.title, y = 0f, width = 330f, height = 24f).apply {
                    textState.fontSize = 18f
                }
                +TextArea(notification.text, 0f, 24f, 330f, 40f).apply {
                    isEditable = false
                    textState.textColor = Config.colorPalette.textColor.toColor()
                    background { darkColor }
                }
            }
        }

        listenerMap.addListener(EventNotificationUpdate::class.java) {
            replaceState(Unit)
        }
    }

    companion object : RComponentSpec<EventPanel, Unit, Unit> {
        val notificationTime = 5000
    }
}