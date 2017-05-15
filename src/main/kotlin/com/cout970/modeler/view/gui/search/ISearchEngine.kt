package com.cout970.modeler.view.gui.search

/**
 * Created by cout970 on 2017/04/12.
 */
interface ISearchEngine {

    fun search(field: String): List<SearchResult>
}