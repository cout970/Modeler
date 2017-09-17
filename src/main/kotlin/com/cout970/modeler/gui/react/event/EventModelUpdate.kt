package com.cout970.modeler.gui.react.event

import com.cout970.modeler.api.model.IModel
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Frame
import org.liquidengine.legui.event.Event
import org.liquidengine.legui.system.context.Context

/**
 * Created by cout970 on 2017/09/16.
 */
class EventModelUpdate(
        comp: Component,
        ctx: Context,
        frame: Frame,
        val newModel: IModel,
        val oldModel: IModel
) : Event<Component>(comp, ctx, frame)