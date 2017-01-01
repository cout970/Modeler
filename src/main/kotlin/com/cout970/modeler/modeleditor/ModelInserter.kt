package com.cout970.modeler.modeleditor

import com.cout970.modeler.model.*
import com.cout970.modeler.modeleditor.selection.ModelPath
import com.cout970.modeler.modeleditor.selection.SelectionGroup
import com.cout970.modeler.util.replaceSelected
import com.cout970.vector.extensions.vec3Of

/**
 * Created by cout970 on 2016/12/09.
 */
class ModelInserter(val modelController: ModelController) {

    var groupCount = 0
    var objCount = 0

    var insertPath = ModelPath(-1, -1)

    var insertPosition = vec3Of(0, 0, 0)

    fun insertComponent(comp: Mesh) {
        if (insertPath.group == -1) {
            insertGroup()
            insertPath = insertPath.copy(group = 0)
        }
        modelController.apply {
            val insertSelection = SelectionGroup(listOf(insertPath))
            updateModel(model.copy(model.objects.replaceSelected(insertSelection) { objIndex, obj ->
                obj.copy(obj.groups.replaceSelected(insertSelection, objIndex) { groupIndex, group ->
                    group.add(comp)
                })
            }))
        }
    }

    fun insertGroup(group: ModelGroup = ModelGroup(listOf(), Transformation(insertPosition), "Group${groupCount++}")) {
        if (insertPath.obj == -1) {
            insertObject()
            insertPath = insertPath.copy(0)
        }
        modelController.apply {
            //there is no object selection so this is a workaround
            val insertSelection = SelectionGroup(listOf(insertPath))
            updateModel(model.copy(model.objects.replaceSelected(insertSelection) { _, obj ->
                obj.add(group)
            }))
        }
    }

    fun insertObject(obj: ModelObject = ModelObject(listOf(), Transformation(insertPosition), "Object${objCount++}",
                                                    Material.MaterialNone)) {
        modelController.apply {
            updateModel(model.add(obj))
        }
    }
}