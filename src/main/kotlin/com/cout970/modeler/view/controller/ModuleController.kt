package com.cout970.modeler.view.controller

import com.cout970.glutilities.event.EnumKeyState
import com.cout970.glutilities.event.EventKeyUpdate
import com.cout970.modeler.config.Config
import com.cout970.modeler.event.IEventController
import com.cout970.modeler.event.IEventListener
import com.cout970.modeler.event.IInput
import com.cout970.modeler.modeleditor.IModelProvider
import com.cout970.modeler.resource.GuiResources
import com.cout970.modeler.view.gui.Root
import com.cout970.modeler.view.module.*

/**
 * Created by cout970 on 2016/12/27.
 */

class ModuleController(
        val modelProvider: IModelProvider,
        val rootFrame: Root,
        val buttonController: ButtonController,
        val input: IInput,
        guiResources: GuiResources) {

    val modules = listOf(
            ModuleAddElement(this),
            ModuleHistoric(this),
            ModuleTexture(this),
            ModuleBoundingBoxes(this),
            ModuleStructure(this)
    ).onEach {
        rootFrame.rightBar.addComponent(it)
    }

    init {
        modules
    }

    fun recalculateModules() {
        var last = 0f
        modules.forEach {
            it.position.y = last
            last = it.position.y + it.size.y + 1
        }
        rootFrame.rightBar.size.y = last
    }

    fun tick() {
        modules.forEach(Module::tick)
    }

    fun registerListeners(eventHandler: IEventController) {

        eventHandler.addListener(EventKeyUpdate::class.java, object : IEventListener<EventKeyUpdate> {
            override fun onEvent(e: EventKeyUpdate): Boolean {
                if (e.keyState != EnumKeyState.RELEASE) {
                    when {
                        Config.keyBindings.delete.check(input) -> buttonController.onClick("input.delete")
                        Config.keyBindings.undo.check(input) -> buttonController.onClick("input.undo")
                        Config.keyBindings.redo.check(input) -> buttonController.onClick("input.redo")
                        Config.keyBindings.copy.check(input) -> buttonController.onClick("input.copy")
                        Config.keyBindings.paste.check(input) -> buttonController.onClick("input.paste")
                        Config.keyBindings.cut.check(input) -> buttonController.onClick("input.cut")
                    }
                }
                return false
            }
        })
    }
}