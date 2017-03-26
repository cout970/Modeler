package com.cout970.modeler.view.controller.selection

import com.cout970.modeler.model.Model
import com.cout970.modeler.util.toJoml3d
import com.cout970.modeler.view.controller.SceneSpaceContext
import com.cout970.modeler.view.scene.Scene
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.vec2Of
import com.cout970.vector.extensions.vec3Of
import org.joml.Matrix4d
import org.joml.Vector3d

/**
 * Created by cout970 on 2017/03/26.
 */

abstract class AbstractCursorTracker {

    abstract fun updateCache(scene: Scene, obj: ISelectable, oldCache: CursorTrackerCache,
                             oldContext: SceneSpaceContext, newContext: SceneSpaceContext): CursorTrackerCache

    abstract fun getPhantomModel(selector: SceneSelector, obj: ISelectable, cache: CursorTrackerCache): Model

    fun projectAxis(matrix: Matrix4d, obj: ISelectable): Pair<IVector2, IVector2> {
        val origin = vec3Of(0)
        val dest = obj.translationAxis

        val start = matrix.project(origin.toJoml3d(), intArrayOf(-1, -1, 2, 2), Vector3d())
        val end = matrix.project(dest.toJoml3d(), intArrayOf(-1, -1, 2, 2), Vector3d())
        return vec2Of(start.x, start.y) to vec2Of(end.x, end.y)
    }
}