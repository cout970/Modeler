package com.cout970.modeler.gui.event

import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.UI
import com.cout970.modeler.gui.setTimeout
import com.cout970.modeler.util.getListeners

class NotificationHandler {
    lateinit var gui: Gui

    init {
        instance = this
    }

    private val notifications = mutableListOf<Notification>()

    fun push(noti: Notification) {
        notifications += noti
        UI.setTimeout(NOTIFICATION_DELAY, this::updateNotifications)
        sendUpdate()
    }

    fun updateNotifications() {
        if (notifications.isNotEmpty()) {
            val now = Timer.miliTime
            notifications.removeAll { now - it.creationTime >= NotificationHandler.NOTIFICATION_DELAY }
            sendUpdate()
        }
    }

    private fun sendUpdate() {
        val listeners = gui.editorView.base.getListeners<EventNotificationUpdate>()
        listeners.forEach { (comp, listener) ->
            listener.process(EventNotificationUpdate(comp, gui.root.context, gui.root, notifications))
        }
    }

    companion object {
        private lateinit var instance: NotificationHandler
        const val NOTIFICATION_DELAY = 5000

        fun push(noti: Notification) = instance.push(noti)

        fun getNotifications() = instance.notifications
    }
}