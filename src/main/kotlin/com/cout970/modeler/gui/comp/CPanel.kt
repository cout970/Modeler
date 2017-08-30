package com.cout970.modeler.gui.comp

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.GuiResources
import com.cout970.modeler.gui.GuiState
import com.cout970.modeler.util.toColor
import org.joml.Vector2f
import org.liquidengine.legui.color.ColorConstants
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Panel
import org.liquidengine.legui.event.ScrollEvent

/**
 * Created by cout970 on 2017/03/12.
 */
open class CPanel(
        x: Float = 0f, y: Float = 0f, width: Float = 10f, height: Float = 10f
) : Panel<Component>(Vector2f(x, y), Vector2f(width, height)) {

    val id = lastID++

    init {
        border = PixelBorder().also { it.isEnabled = false }
        cornerRadius = 0f
        backgroundColor = Config.colorPalette.darkColor.toColor()
        listenerMap.addListener(ScrollEvent::class.java) {
            propagateScroll(it)
        }
    }

    fun propagateScroll(e: ScrollEvent<*>) {
        val parent = parent?.parent?.parent
        if (parent is CVerticalPanel) {
            parent.propagateScroll(e)
        }
    }

    companion object {
        private var lastID = 0
    }

    open fun loadResources(resources: GuiResources) {
        childs.filterIsInstance<CPanel>()
                .forEach { it.loadResources(resources) }
    }

    open fun bindProperties(state: GuiState) {
        childs.forEach {
            when (it) {
                is CPanel -> it.bindProperties(state)
                is CToggleButton -> it.bindState(state)
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CPanel) return false
        if (!super.equals(other)) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + id
        return result
    }

    override fun toString(): String {
        return "CPanel(id=$id)"
    }
}

fun Component.setBorderless() {
    border.isEnabled = false
}

fun Component.setTransparent() {
    backgroundColor = ColorConstants.transparent()
}