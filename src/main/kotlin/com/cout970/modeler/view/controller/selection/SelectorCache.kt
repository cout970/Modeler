package com.cout970.modeler.view.controller.selection

import com.cout970.modeler.model.Model
import com.cout970.modeler.view.controller.SceneSpaceContext

/**
 * Created by cout970 on 2017/03/26.
 */

class SelectorCache {
    var currentContext: SceneSpaceContext? = null
    var oldContext: SceneSpaceContext? = null
    var hoveredObject: ISelectable? = null
    var selectedObject: ISelectable? = null
    var model: Model? = null
    var cursorCache: CursorTrackerCache = CursorTrackerCache()
}