package com.cout970.modeler.view

import com.cout970.glutilities.event.EventKeyUpdate
import com.cout970.modeler.controller.CommandExecutor
import com.cout970.modeler.core.config.Config

/**
 * Created by cout970 on 2017/06/10.
 */
@Deprecated("Use KeyboardBinder instead")
class HotKeyHandler(val exec: CommandExecutor) {

    fun onPress(e: EventKeyUpdate): Boolean {
        Config.keyBindings.apply {
            when {
                switchOrthoProjection.check(e) -> exec.execute("view.switch.ortho")
                setTextureMode.check(e) -> exec.execute("view.set.texture.mode")
                setModelMode.check(e) -> exec.execute("view.set.model.mode")
                else -> return false
            }
            return true
        }
        return false
    }
}