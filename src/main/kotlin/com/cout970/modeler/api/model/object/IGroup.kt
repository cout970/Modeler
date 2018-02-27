package com.cout970.modeler.api.model.`object`

import com.cout970.modeler.api.model.selection.IObjectRef
import java.util.*

interface IGroup {
    val name: String
    val id: UUID
}

interface IGroupTree {
    val root: List<IGroup>

    fun addGroup(parent: IGroup?, newGroup: IGroup): IGroupTree

    fun removeGroup(parent: IGroup?, child: IGroup): IGroupTree

    fun changeParent(child: IGroup, newParent: IGroup): IGroupTree

    fun addObject(parent: IGroup, child: IObjectRef): IGroupTree

    fun removeObject(parent: IGroup, child: IObjectRef): IGroupTree

    fun getChildren(parent: IGroup): List<IGroup>

    fun getParent(child: IGroup): IGroup?

    fun getGroup(obj: IObjectRef): IGroup?

    fun getObjects(group: IGroup): List<IObjectRef>
}