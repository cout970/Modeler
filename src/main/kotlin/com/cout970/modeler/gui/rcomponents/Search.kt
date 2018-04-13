package com.cout970.modeler.gui.rcomponents

import com.cout970.glutilities.device.Keyboard
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.core.search.SearchDatabase
import com.cout970.modeler.util.focus
import com.cout970.reactive.core.*
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.comp
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.label
import com.cout970.reactive.nodes.style
import org.liquidengine.legui.component.TextInput
import org.liquidengine.legui.component.event.textinput.TextInputContentChangeEvent
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.event.KeyEvent
import org.liquidengine.legui.event.MouseClickEvent
import org.liquidengine.legui.system.context.Context
import org.lwjgl.glfw.GLFW
import kotlin.math.min

data class SearchResult(val text: String, val keyBind: String, val cmd: String)
data class SearchState(val visible: Boolean, val text: String, val results: List<SearchResult>, val selected: Int) : RState

class SearchProps() : RProps

private const val MAX_SEARCH_RESULTS = 40

class Search : RComponent<SearchProps, SearchState>() {

    private var ctx: Context? = null

    override fun getInitialState() = SearchState(false, "", emptyList(), 0)

    override fun RBuilder.render() = div("Search") {

        style {
            transparent()
            borderless()
            sizeX = 480f

            if (!state.visible) {
                hide()
            }
        }

        postMount {
            posY = 100f
            sizeY = parent.sizeY - posY - posY
            centerX()
        }

        div {
            style {
                posY = 4f
                sizeY = 60f
                background { darkColor }
                border(4f) { darkestColor }
            }

            postMount {
                marginX(4f)
            }

            label("Search everywhere") {
                style {
                    posX = 5f
                    posY = 4f
                    defaultTextColor()
                    fontSize(20f)
                }
            }

            comp(TextInput(state.text)) {

                style {
                    fontSize = 20f
                    posX = 5f
                    posY = 24f
                    sizeY = 30f
                    defaultTextColor()
                    background { lightBrightColor }
                }

                postMount {
                    sizeX = parent.sizeX - posX - posX
                    if (state.visible) {
                        ctx?.focus(this)
                    }
                }

                onFocus {
                    if (!it.isFocused) {
                        println(it.nextFocus)
                        setState { getInitialState() }
                    }
                }

                on<TextInputContentChangeEvent<*>> {
                    setState { copy(text = it.newValue, results = SearchDatabase.search(it.newValue)) }
                }

                onKey(this@Search::onUpdate)
            }
        }

        if (state.results.isNotEmpty()) {

            div("SearchResults") {

                style {
                    posY = 60f + 8f
                    sizeX = 480f
                }

                postMount {
                    sizeY = min(parent.sizeY - posY, childComponents.size * 24f)
                }

                state.results.forEachIndexed { index, result ->
                    searchResult(index, result)
                }
            }
        }

        onCmd("showSearch") {
            ctx = it["ctx"] as Context
            setState { copy(visible = true) }
        }
    }

    private fun RBuilder.searchResult(index: Int, result: SearchResult) = div {
        style {
            background {
                if (index == state.selected) selectedOption else greyColor
            }
            sizeX = 480f
            sizeY = 24f
            posY = index * sizeY
        }

        label(result.text) {
            style {
                posX = 5f
                posY = 0f
                sizeX = 200f
                sizeY = 24f
                defaultTextColor()
            }
        }

        label(result.keyBind) {
            style {
                posX = 270f
                posY = 0f
                sizeX = 200f
                sizeY = 24f
                horizontalAlign = HorizontalAlign.RIGHT
                defaultTextColor()
            }
        }

        onClick {
            if (it.action == MouseClickEvent.MouseClickAction.CLICK) {
                setState { copy(selected = index) }
            }
        }
    }

    private fun onUpdate(e: KeyEvent<*>) {
        if (e.action == GLFW.GLFW_RELEASE) return

        if (e.key == Keyboard.KEY_UP && state.selected > 0) {
            setState { copy(selected = selected - 1) }
        } else if (e.key == Keyboard.KEY_DOWN && Math.min(state.results.size, MAX_SEARCH_RESULTS) > state.selected + 1) {
            setState { copy(selected = selected + 1) }
        } else if (e.key == Keyboard.KEY_ENTER) {
            setState { getInitialState() }

            println("Running ${state.results[state.selected]}")
        } else if (e.key == Keyboard.KEY_ESCAPE) {
            setState { getInitialState() }
            return
        }
    }
}