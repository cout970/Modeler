package com.cout970.modeler.controller

import com.cout970.modeler.api.model.IModel

interface IModelGetter {
    val model: IModel
}

interface IModelSetter : IModelGetter {
    override var model: IModel
}