package com.cout970.modeler.view.gui.search

import com.cout970.glutilities.device.Keyboard
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.util.hide
import com.cout970.modeler.util.show
import com.cout970.modeler.util.toColor
import com.cout970.modeler.view.gui.comp.CPanel
import org.liquidengine.legui.component.Label
import org.liquidengine.legui.component.TextInput
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.event.FocusEvent
import org.liquidengine.legui.event.KeyEvent
import org.lwjgl.glfw.GLFW

/**
 * Created by cout970 on 2017/04/12.
 */
class SearchPanel(val modelview: SearchFacade) : CPanel(width = 400f, height = 60f) {

    val label = Label("Search", 5f, 2f, 100f, 24f)
    val searchBar = TextInput("", 10f, 28f, 380f, 20f)
    val searchResults = CPanel(0f, 0f, width = 400f, height = 60f)
    var selectedOption = -1
    val maxSearchResults = 20

    init {
        searchBar.textState.fontSize = 20f
        searchBar.textState.textColor = Config.colorPalette.textColor.toColor()
        label.textState.textColor = Config.colorPalette.textColor.toColor()
        add(label)
        add(searchBar)
        backgroundColor = Config.colorPalette.darkColor.toColor()
        searchBar.backgroundColor = Config.colorPalette.lightColor.toColor()

        searchBar.listenerMap.addListener(KeyEvent::class.java, this::onUpdate)

        searchBar.listenerMap.addListener(FocusEvent::class.java) {
            if (!it.isFocused) {
                searchResults.hide()
                searchBar.textState.text = ""
                hide()
            }
        }

        searchResults.hide()
        hide()
    }

    private fun onUpdate(e: KeyEvent<*>) {
        if (e.action != GLFW.GLFW_PRESS) return

        if (e.key == Keyboard.KEY_UP && selectedOption > 0) {
            selectedOption--
        } else if (e.key == Keyboard.KEY_DOWN && Math.min(modelview.searchResults.size,
                maxSearchResults) > selectedOption + 1) {

            selectedOption++
        } else if (e.key == Keyboard.KEY_ENTER) {
            searchResults.hide()
            this.hide()
            searchBar.textState.text = ""
            if (selectedOption in modelview.searchResults.indices) {
                modelview.execute(selectedOption)
            }
            return
        } else if (e.key == Keyboard.KEY_ESCAPE) {
            searchResults.hide()
            this.hide()
            searchBar.textState.text = ""
            return
        } else if (e.key != Keyboard.KEY_UP && e.key != Keyboard.KEY_DOWN) {
            modelview.performSearch(searchBar.textState.text)
            selectedOption = 0
        }
        renderResults(modelview)
    }

    private fun renderResults(modelview: SearchFacade) {

        searchResults.clearChilds()
        searchResults.show()

        val displayedResults = modelview.searchResults.take(maxSearchResults)
        displayedResults.forEachIndexed { index, (text, keyBind) ->
            val panel = CPanel(0f, index * 20f, 400f, 24f).also {
                if (selectedOption == index) {
                    it.backgroundColor = Config.colorPalette.selectedOption.toColor()
                } else {
                    it.backgroundColor = Config.colorPalette.primaryColor.toColor()
                }
            }

            panel.add(Label(text, 5f, 0f, 200f, 24f).also {
                it.textState.textColor = Config.colorPalette.textColor.toColor()
            })
            panel.add(Label(keyBind, 195f, 0f, 200f, 24f).also {
                it.textState.horizontalAlign = HorizontalAlign.RIGHT
                it.textState.textColor = Config.colorPalette.textColor.toColor()
            })

            searchResults.add(panel)
        }
        searchResults.size.y = displayedResults.size * 20f + 2f
    }

}