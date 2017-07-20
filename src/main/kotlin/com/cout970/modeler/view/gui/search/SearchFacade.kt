package com.cout970.modeler.view.gui.search

/**
 * Created by cout970 on 2017/04/12.
 */

class SearchFacade() {

    val searchEngine: ISearchEngine = SearchDatabase
    val searchResults = mutableListOf<SearchResult>()

    fun performSearch(searchParam: String) {
        searchResults.clear()
        searchResults += searchEngine.search(searchParam)
    }

    fun execute(selectedOption: Int) {
//        commandExecutor.execute(searchResults[selectedOption].cmd)
    }
}