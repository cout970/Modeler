package com.cout970.modeler.model.structure

import com.cout970.modeler.model.Edge
import com.cout970.modeler.model.Quad
import com.cout970.modeler.model.Vertex

/**
 * Created by cout970 on 2017/02/16.
 */
data class VertexStructure(
        val quads: List<Quad>,
        val edges: List<Edge>,
        val vertex: List<Vertex>
)