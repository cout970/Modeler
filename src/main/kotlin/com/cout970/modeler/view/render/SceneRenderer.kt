package com.cout970.modeler.view.render

import com.cout970.modeler.view.render.comp.IRenderableComponent

/**
 * Created by cout970 on 2017/01/23.
 */
abstract class SceneRenderer(val shaderHandler: ShaderHandler) {

    abstract val components: Map<ShaderType, List<IRenderableComponent>>

}