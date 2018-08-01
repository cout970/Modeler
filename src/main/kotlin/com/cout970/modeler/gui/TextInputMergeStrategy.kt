package com.cout970.modeler.gui

import com.cout970.reactive.core.IMergeStrategy
import com.cout970.reactive.core.Renderer
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.TextInput

object TextInputMergeStrategy : IMergeStrategy {

    override fun merge(old: Component, new: Component): Component {
        // Move childs to the new tree to be checked and updated by ReconciliationManager#traverse
        new.clearChildComponents()
        new.addAll(old.childComponents)

        // Move old components to the new tree to keep their state
        old.metadata[Renderer.METADATA_COMPONENTS]?.let { compStates ->
            new.metadata[Renderer.METADATA_COMPONENTS] = compStates
        }

        new as TextInput
        old as TextInput

        new.startSelectionIndex = old.startSelectionIndex
        new.endSelectionIndex = old.endSelectionIndex
        new.caretPosition = old.caretPosition
        new.mouseCaretPosition = old.mouseCaretPosition
        return new
    }
}