package com.cout970.modeler.view

import com.cout970.glutilities.event.EventKeyUpdate
import com.cout970.modeler.controller.CommandExecutor
import com.cout970.modeler.core.config.Config

/**
 * Created by cout970 on 2017/06/10.
 */
class HotKeyHandler(val exec: CommandExecutor) {

    fun onPress(e: EventKeyUpdate): Boolean {
        Config.keyBindings.apply {
            when {
                saveProject.check(e) -> exec.execute("project.save")
                saveProjectAs.check(e) -> exec.execute("project.save.as")
                exportModel.check(e) -> exec.execute("project.export")
                importModel.check(e) -> exec.execute("project.import")
                delete.check(e) -> exec.execute("model.selection.delete")
                else -> return false
            }
            return true
        }
        return false
    }
}