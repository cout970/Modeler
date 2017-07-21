package com.cout970.modeler.view.gui.comp

import com.cout970.modeler.controller.GuiState
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.util.toColor
import com.cout970.modeler.view.GuiResources
import org.joml.Vector2f
import org.liquidengine.legui.border.SimpleLineBorder
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Panel

/**
 * Created by cout970 on 2017/03/12.
 */
open class CPanel(
        x: Float = 0f, y: Float = 0f, width: Float = 10f, height: Float = 10f
) : Panel<Component>(Vector2f(x, y), Vector2f(width, height)) {

    val id = lastID++

    init {
        border = SimpleLineBorder().apply {
            thickness = 0.5f
            color = Config.colorPalette.borderColor.toColor()
        }
        backgroundColor = Config.colorPalette.lightColor.toColor()
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

    fun setBorderless() {
        border.isEnabled = false
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
}