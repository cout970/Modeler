package com.cout970.modeler.gui.event

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.util.Nullable
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

class EventMaterialUpdate(
        comp: Component,
        ctx: Context,
        frame: Frame,
        val newMaterial: Nullable<IMaterial>,
        val oldMaterial: Nullable<IMaterial>
) : Event<Component>(comp, ctx, frame)

class EventSelectionUpdate(
        comp: Component,
        ctx: Context,
        frame: Frame,
        val newSelection: Nullable<ISelection>,
        val oldSelection: Nullable<ISelection>
) : Event<Component>(comp, ctx, frame)

class EventSelectionTypeUpdate(
        comp: Component,
        ctx: Context,
        frame: Frame,
        val newSelectionType: SelectionType,
        val oldSelectionType: SelectionType
) : Event<Component>(comp, ctx, frame)

class EventNotificationUpdate(
        comp: Component,
        ctx: Context,
        frame: Frame,
        val notifications: List<Notification>
) : Event<Component>(comp, ctx, frame)
