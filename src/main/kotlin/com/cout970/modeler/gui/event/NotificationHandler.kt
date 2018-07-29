package com.cout970.modeler.gui.event

import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.UI
import com.cout970.modeler.gui.setTimeout
import java.util.*

class NotificationHandler {
    lateinit var gui: Gui

    init {
        instance = this
    }

    private val notifications = Collections.synchronizedList(mutableListOf<Notification>())

    fun push(noti: Notification) {
        notifications += noti
        UI.setTimeout(NOTIFICATION_DELAY, this::updateNotifications)
        sendUpdate()
    }

    fun remove(noti: Notification) {
        notifications.remove(noti)
    }

    fun updateNotifications() {
        if (notifications.isNotEmpty()) {
            val now = Timer.miliTime
            notifications.removeAll { now - it.creationTime >= NotificationHandler.NOTIFICATION_DELAY }
            sendUpdate()
        }
    }

    private fun sendUpdate() = gui.listeners.runGuiCommand("updateNotifications")

    companion object {
        private lateinit var instance: NotificationHandler
        const val NOTIFICATION_DELAY = 5000

        fun push(noti: Notification) = instance.push(noti)

        fun remove(noti: Notification) = instance.remove(noti)

        fun getNotifications(): MutableList<Notification> = instance.notifications
    }
}

fun pushNotification(title: String, text: String) {
    NotificationHandler.push(Notification(title, text))
}