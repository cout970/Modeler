package com.cout970.modeler.view.module

import com.cout970.modeler.view.controller.ModuleController
import com.cout970.modeler.view.gui.TextureHandler
import com.cout970.modeler.view.gui.comp.CButton
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.event.component.MouseClickEvent

/**
 * Created by cout970 on 2016/12/27.
 */
class ModuleTransform(controller: ModuleController, textureHandler: TextureHandler) : Module(controller, "Transform") {

    init {
        addSubComponent(CButton("Translate", 5f, 5f, 180f, 20f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener("menu.cursor.translation"))
            textState.horizontalAlign = HorizontalAlign.LEFT
            textState.padding.x = 30f
            setImage(textureHandler.cursorTranslate)
        })
        addSubComponent(CButton("Rotate", 5f, 25f, 180f, 20f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener("menu.cursor.rotation"))
            textState.horizontalAlign = HorizontalAlign.LEFT
            textState.padding.x = 30f
            setImage(textureHandler.cursorRotate)
        })
        addSubComponent(CButton("Scale", 5f, 45f, 180f, 20f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener("menu.cursor.scale"))
            textState.horizontalAlign = HorizontalAlign.LEFT
            textState.padding.x = 30f
            setImage(textureHandler.cursorScale)
        })
        maximize()
    }
}