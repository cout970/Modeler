package com.cout970.modeler.core.collision

import com.cout970.collision.IPolygon
import com.cout970.vector.api.IVector2

data class PointPolygon(val point: IVector2) : IPolygon {

    override fun getEdges(): List<Pair<IVector2, IVector2>> = listOf(point to point)

    override fun getVertex(): List<IVector2> = listOf(point)
}