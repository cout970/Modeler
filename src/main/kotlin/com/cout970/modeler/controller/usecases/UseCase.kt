package com.cout970.modeler.controller.usecases


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class UseCase(val key: String)