package com.cout970.modeler.core.project

import com.cout970.modeler.core.export.ExportManager
import com.cout970.modeler.to_redo.modeleditor.ModelEditor

/**
 * Created by cout970 on 2017/01/04.
 */
class ProjectManager {

    var project = Project(Author("Anonymous", ""), "Unnamed")

    lateinit var exportManager: ExportManager
    lateinit var modelEditor: ModelEditor
}