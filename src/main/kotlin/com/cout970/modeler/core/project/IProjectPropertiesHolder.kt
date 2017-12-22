package com.cout970.modeler.core.project

interface IProjectPropertiesHolder {

    val projectProperties: ProjectProperties

    fun updateProperties(props: ProjectProperties)
}

class ProjectPropertyHolder(val projectManager: ProjectManager) : IProjectPropertiesHolder {
    override val projectProperties: ProjectProperties
        get() = projectManager.projectProperties

    override fun updateProperties(props: ProjectProperties) {
        projectManager.loadProjectProperties(props)
    }
}