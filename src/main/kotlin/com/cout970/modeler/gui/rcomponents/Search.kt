package com.cout970.modeler.gui.rcomponents

import com.cout970.modeler.gui.search.SearchDatabase
import com.cout970.reactive.core.EmptyProps
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RComponent
import com.cout970.reactive.core.RState
import com.cout970.reactive.nodes.comp
import com.cout970.reactive.nodes.div
import org.liquidengine.legui.component.TextInput
import org.liquidengine.legui.component.event.textinput.TextInputContentChangeEvent

data class SearchResult(val text: String, val keyBind: String, val cmd: String)


data class SearchState(val text: String, val results: List<SearchResult>) : RState

class Search : RComponent<EmptyProps, SearchState>() {
    override fun getInitialState() = SearchState("", emptyList())


    override fun RBuilder.render() = div("Search") {

        comp(TextInput()) {

            on<TextInputContentChangeEvent<*>> {
                setState { SearchState(it.newValue, SearchDatabase.search(it.newValue)) }
            }
        }
    }
}