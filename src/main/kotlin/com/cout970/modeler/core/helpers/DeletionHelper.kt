package com.cout970.modeler.core.helpers

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IGroupRef
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.selection.*
import com.cout970.modeler.core.model.Model
import com.cout970.modeler.core.model.`object`.Object
import com.cout970.modeler.core.model.`object`.ObjectCube
import com.cout970.modeler.core.model.getRecursiveChildGroups
import com.cout970.modeler.core.model.getRecursiveChildObjects
import com.cout970.modeler.core.model.mesh.Mesh
import com.cout970.modeler.core.model.objects

object DeletionHelper {

    fun delete(source: IModel, selection: ISelection): IModel {
        if (selection.selectionTarget != SelectionTarget.MODEL) return source
        return when (selection.selectionType) {
            SelectionType.OBJECT -> {
                source.removeObjects(selection.objects)
            }
            SelectionType.FACE -> {
                val toRemove = mutableListOf<IObjectRef>()
                val edited = mutableMapOf<IObjectRef, IObject>()

                source.objectMap.forEach { objIndex, obj ->
                    if (obj is Object) {
                        val modifyMesh = obj.mesh.let {
                            val newFaces = it.faces.mapIndexedNotNull { index, iFaceIndex ->
                                val ref = objIndex.toFaceRef(index)
                                if (selection.isSelected(ref)) null else iFaceIndex
                            }
                            if (newFaces == it.faces) it else Mesh(it.pos, it.tex, newFaces)
                        }

                        if (modifyMesh.faces.isNotEmpty()) {
                            edited += objIndex to obj.copy(mesh = modifyMesh)
                        } else {
                            toRemove += objIndex
                        }

                    } else if (obj is ObjectCube) {
                        val modifyMesh = obj.mesh.let {
                            val newFaces = it.faces.mapIndexedNotNull { index, iFaceIndex ->
                                val ref = objIndex.toFaceRef(index)
                                if (selection.isSelected(ref)) null else iFaceIndex
                            }
                            if (newFaces == it.faces) it else Mesh(it.pos, it.tex, newFaces)
                        }

                        if (modifyMesh.faces.isNotEmpty()) {
                            edited += objIndex to obj.withMesh(modifyMesh)
                        } else {
                            toRemove += objIndex
                        }
                    }
                }
                source.modifyObjects(edited.keys) { ref, _ -> edited[ref]!! }
            }
            SelectionType.EDGE, SelectionType.VERTEX -> source
        }
    }
}