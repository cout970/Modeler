package com.cout970.modeler.api.model.`object`

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.IObjectRef
import java.util.*

// Groups

interface IGroup {
    val id: UUID
    val name: String
    val visible: Boolean

    fun withVisibility(visible: Boolean): IGroup
    fun withName(name: String): IGroup
}

data class Group(
        override val name: String,
        override val visible: Boolean = true,
        override val id: UUID = UUID.randomUUID()
) : IGroup {
    override fun withVisibility(visible: Boolean): IGroup = copy(visible = visible)
    override fun withName(name: String): IGroup = copy(name = name)
}

object GroupNone : IGroup {
    override val id: UUID = RootGroupRef.id
    override val name: String = "root"
    override val visible: Boolean = true

    override fun withVisibility(visible: Boolean): IGroup = this
    override fun withName(name: String): IGroup = this
}

// GroupRefs

interface IGroupRef {
    val id: UUID
}

object RootGroupRef : IGroupRef {
    override val id: UUID = UUID.fromString("713e7999-081e-488c-9ced-17d010bdd270")
}

data class GroupRef(override val id: UUID = UUID.randomUUID()) : IGroupRef {

    override fun equals(other: Any?): Boolean = (other as? IGroupRef)?.id == id

    override fun hashCode(): Int = id.hashCode()
}

/**
 * This represents the links between groups and objects but doesn't depend on the
 * actual instances, so they can be changed keeping the links
 */
interface IGroupTree {

    fun addGroup(parent: IGroupRef, newGroupRef: IGroupRef): IGroupTree

    fun removeGroup(parent: IGroupRef, child: IGroupRef): IGroupTree

    fun changeParent(child: IGroupRef, newParent: IGroupRef): IGroupTree

    fun getChildren(parent: IGroupRef): List<IGroupRef>

    fun getParent(child: IGroupRef): IGroupRef

    fun merge(other: IGroupTree): IGroupTree
}