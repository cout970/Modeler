package com.cout970.modeler.newView.search

import com.cout970.modeler.newView.ButtonController

/**
 * Created by cout970 on 2017/04/12.
 */

class ModelView(val buttonController: ButtonController) {

    val searchEngine: ISearchEngine = SearchDatabase
    val searchResults = mutableListOf<SearchResult>()

    fun performSearch(searchParam: String) {
        searchResults.clear()
        searchResults += searchEngine.search(searchParam)
    }

    fun execute(selectedOption: Int) {
        buttonController.onClick(searchResults[selectedOption].cmd)
    }
}