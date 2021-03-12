package com.cout970.modeler.gui.rcomponents

import com.cout970.glutilities.device.Keyboard
import com.cout970.modeler.controller.Dispatch
import com.cout970.modeler.core.search.SearchDatabase
import com.cout970.modeler.gui.leguicomp.classes
import com.cout970.modeler.gui.leguicomp.defaultTextColor
import com.cout970.modeler.gui.leguicomp.fontSize
import com.cout970.modeler.gui.leguicomp.onCmd
import com.cout970.modeler.util.focus
import com.cout970.reactive.core.EmptyProps
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RComponent
import com.cout970.reactive.core.RState
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


private const val MAX_SEARCH_RESULTS = 40

class Search : RComponent<EmptyProps, SearchState>() {

    private var searchCtx: Context? = null

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

        div("Search panel") {
            style {
                posY = 4f
                sizeY = 60f
                classes("search_bar")
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

            comp(TextInput(state.text), "search_bar") {

                style {
                    fontSize = 20f
                    posX = 5f
                    posY = 24f
                    sizeY = 30f
                    defaultTextColor()
                    classes("search_bar_input")
                    horizontalAlign = HorizontalAlign.LEFT
                }

                postMount {
                    sizeX = parent.sizeX - posX - posX
                    if (state.visible) {
                        searchCtx?.focus(this)
                    }
                }

                onFocus {
                    if (!it.isFocused) {
                        setState { getInitialState() }
                    }
                }

                on<TextInputContentChangeEvent<*>> {
                    setState { copy(text = it.newValue, results = SearchDatabase.search(it.newValue)) }
                }

                onKey(this@Search::onUpdate)
            }
        }

        div("SearchResults") {

            style {
                posX = 4f
                posY = 60f + 8f
                sizeX = 472f
                classes("search_bar_results")
            }

            postMount {
                sizeY = min(parent.sizeY - posY, childComponents.size * 24f)
            }

            state.results.forEachIndexed { index, result ->
                searchResult(index, result)
            }
        }

        onCmd("showSearch") {
            searchCtx = it["ctx"] as Context
            setState { copy(visible = true) }
        }
    }

    private fun RBuilder.searchResult(index: Int, result: SearchResult) = div {
        style {
            classes("search_bar_result")
            if (index == state.selected) {
                classes("search_bar_result_selected")
            }
            sizeX = 470f
            sizeY = 24f
            posY = index * sizeY
        }

        label(result.text) {
            style {
                posX = 8f
                posY = 0f
                sizeX = 200f
                sizeY = 24f
                defaultTextColor()
                fontSize(20f)
            }
        }

        label(result.keyBind) {
            style {
                posX = 260f
                posY = 0f
                sizeX = 200f
                sizeY = 24f
                horizontalAlign = HorizontalAlign.RIGHT
                defaultTextColor()
                fontSize(20f)
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
        } else if (e.key == Keyboard.KEY_DOWN && min(state.results.size, MAX_SEARCH_RESULTS) > state.selected + 1) {
            setState { copy(selected = selected + 1) }
        } else if (e.key == Keyboard.KEY_ENTER) {
            Dispatch.run(state.results[state.selected].cmd)
            setState { getInitialState() }
        } else if (e.key == Keyboard.KEY_ESCAPE) {
            setState { getInitialState() }
            return
        }
    }
}