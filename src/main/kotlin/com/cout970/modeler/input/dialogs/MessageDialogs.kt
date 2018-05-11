package com.cout970.modeler.input.dialogs

import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.log.print
import org.lwjgl.util.tinyfd.TinyFileDialogs

object MessageDialogs {

    fun warningBoolean(title: String, message: String, default: Boolean): Boolean {
        return try {
            TinyFileDialogs.tinyfd_messageBox(
                    title,
                    message,
                    "yesno",
                    "warning",
                    default
            )
        } catch (e: Exception) {
            log(Level.ERROR){
                "Error on a warning (Boolean) dialog: title='$title', message='$message', " +
                        "defaultValue='$default'"
            }
            e.print()
            default
        }
    }
}